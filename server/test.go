package main

import (
	"fmt"
	"time"
	"github.com/google/uuid"
	"github.com/joho/godotenv"
	"os"
	"strings"
	"encoding/pem"
	"crypto/x509"
	jwt "github.com/dgrijalva/jwt-go"
)

func GenerateJWT() { 
	err := godotenv.Load(".env")
	if err != nil { 
		panic(err)
	}

	private_key := os.Getenv("PRIVATE_KEY")
	raw := []byte(strings.ReplaceAll(private_key, "\\n", "\n"))
	block, _ := pem.Decode(raw)
	key, err := x509.ParsePKCS8PrivateKey(block.Bytes)

	if err != nil { 
		fmt.Println(err)
		return
	}

	token := jwt.New(jwt.SigningMethodES256)
	
	claims := token.Claims.(jwt.MapClaims)

	iat := time.Now()
	claims["iat"] = iat.Unix()
	claims["nonce"] = uuid.NewString()
	claims["type"] = "users.create"

	tokenString, err := token.SignedString(key)

	if err != nil {
		fmt.Println(err)
	} else { 
		fmt.Println(tokenString)
	}
}

func main() { 
	GenerateJWT()
}
