package token

import (
	"time"
	"strings"
	"os"
	"encoding/json"
	"encoding/pem"
	"encoding/base64"
	"net/http"	
	"crypto/x509"
	"crypto/ecdsa"
	"crypto/sha256"
	"io/ioutil"
	"errors"
	"github.com/google/uuid"
	"github.com/dgrijalva/jwt-go"
	"github.com/loginid1/ios-example/server/utils"
)

type TokenRequest struct { 
	Type string `json:"type"`
	TXPayload string `json:"tx_payload"`
	Nonce string `json:"nonce"`
}

type Token struct { 
	Token string `json:"token"`
	Username string `json:"username,omitempty"`
	Nonce string `json:"nonce,omitempty"`
	ServerNonce string `json:"server_nonce,omitempty"`
}

type TXVerifyRequest struct {
	Token string `json:"token"`
	TXPayload string `json:"tx_payload"`
}

type Header struct {
	Kid string `json:"kid"`
	Alg string `json:"alg"`
	Type string `json:"typ"`
}

type ServiceToken struct {
	ClientID string `json:"client_id"`
	Type string `json:"type"`
	Iat int `json:"iat"`
	Username string `json:"username,omitempty"`
	UserID string `json:"user_id,omitempty"`
	Nonce string `json:"nonce,omitempty"`
	ServerNonce string `json:"server_nonce,omitempty"`
	TXHash string `json:"tx_hash,omitempty"`
}

type ValidateTokenResponse struct {
	Valid bool `json:"valid"`
}

var ERROR_MESSAGE = "Invalid Token"

func DecodeHeader(headerString string) (*Header, error) {
	var err error

	var decodedHeader []byte
	if decodedHeader, err = base64.RawStdEncoding.DecodeString(headerString); err != nil {
		return nil, err
	}
	
	h := &Header{}
	if err := json.Unmarshal(decodedHeader, h); err != nil {
		return nil, err
	}

	return h, nil
}

func DecodePayload(body string) (*ServiceToken, error) { 
	var err error

	decodePayload, err := base64.RawStdEncoding.DecodeString(body)
	if err != nil {
		return nil, err
	}

	var p ServiceToken
	if err := json.Unmarshal(decodePayload, &p); err != nil {
		return nil, err
	}

	return &p, nil
} 

func GetPublicKey(kid string) (string, error) {
	baseUrl := os.Getenv("BASE_URL")
	url := baseUrl + "/certs?kid=" + kid

	res, err := http.Get(url)
	if err != nil {
		return "", err
	}

	bodyBytes, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return "", err
	}

	return string(bodyBytes), nil
}

func ValidateToken(parts []string) error { 
	if len(parts) != 3 {
		return errors.New(ERROR_MESSAGE)
	}

	h, err := DecodeHeader(parts[0])
	if err != nil {
		return err
	}
	
	if h.Alg != "ES256" { 
		return errors.New(ERROR_MESSAGE)
	}

	publicKey, err := GetPublicKey(h.Kid)
	if err != nil {
		return err
	}

	var ecdsaKey *ecdsa.PublicKey
	if ecdsaKey, err = jwt.ParseECPublicKeyFromPEM([]byte(publicKey)); err != nil {
		return err
	}

	method := jwt.GetSigningMethod(h.Alg)
	err = method.Verify(strings.Join(parts[0:2], "."), parts[2], ecdsaKey)
	if err != nil {
		return err
	}

	return nil
}

func CreateToken(w http.ResponseWriter, r *http.Request) { 
	decoder := json.NewDecoder(r.Body)
	var tk TokenRequest
	err := decoder.Decode(&tk)
	if err != nil {
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	if len(tk.Type) == 0 { 
		errRes.CreateSendError(w, `"type" param needed`, 400)
		return
	}

	pkeyString := strings.ReplaceAll(os.Getenv("PRIVATE_KEY"), "\\n", "\n")
	block, _ := pem.Decode([]byte(pkeyString))
	key, err := x509.ParsePKCS8PrivateKey(block.Bytes)
	if err != nil { 
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	tokenBody := jwt.MapClaims{}
	tokenBody["iat"] = time.Now().Unix()
	tokenBody["type"] = tk.Type

	if tk.Nonce != "" { 
		tokenBody["nonce"] = tk.Nonce
	} else { 
		tokenBody["nonce"] = uuid.NewString()
	}

	if tk.TXPayload != "" {
		hash := sha256.Sum256([]byte(tk.TXPayload))
		ph := base64.RawStdEncoding.EncodeToString(hash[:])
		tokenBody["payload_hash"] = ph
	}

	token := jwt.NewWithClaims(jwt.SigningMethodES256, tokenBody)
	tokenString, err := token.SignedString(key)
	if err != nil { 
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	tokenResponse := Token {
		Token: tokenString,
	}

	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(200)
	rawBody, _ := json.Marshal(tokenResponse)
	w.Write(rawBody)
}

func VerifyToken(w http.ResponseWriter, r *http.Request) { 
	decoder := json.NewDecoder(r.Body)
	var payload Token
	err := decoder.Decode(&payload)
	if err != nil {
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	parts := strings.Split(payload.Token, ".")

	err = ValidateToken(parts)
	if err != nil {
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	} 

	p, err := DecodePayload(parts[1])
	if err != nil { 
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	if payload.Username != "" && payload.Username != p.Username {
		errRes.CreateSendError(w, ERROR_MESSAGE, http.StatusBadRequest)
		return
	}

	res := ValidateTokenResponse {
		Valid: true,
	}

	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(200)
	rawBody, _ := json.Marshal(res)
	w.Write(rawBody)
}

func VerifyTransactionToken(w http.ResponseWriter, r *http.Request) { 
	decoder := json.NewDecoder(r.Body)

	var payload TXVerifyRequest
	err := decoder.Decode(&payload)
	if err != nil {
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	parts := strings.Split(payload.Token, ".")

	err = ValidateToken(parts)
	if err != nil {
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	} 

	p, err := DecodePayload(parts[1])
	if err != nil { 
		errRes.CreateSendError(w, err.Error(), http.StatusBadRequest)
		return
	}

	toHash := payload.TXPayload + p.Nonce + p.ServerNonce
	hash := sha256.Sum256([]byte(toHash))
	ph := base64.RawURLEncoding.EncodeToString(hash[:])

	if ph != p.TXHash {
		errRes.CreateSendError(w, ERROR_MESSAGE, http.StatusBadRequest)
		return
	}

	res := ValidateTokenResponse {
		Valid: true,
	}

	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(200)
	rawBody, _ := json.Marshal(res)
	w.Write(rawBody)
}
