package errRes

import (
	"net/http"
	"encoding/json"
)

type Error struct { 
	Message string `json:"message"`
	Code int `json:"code"`
}

func CreateSendError(w http.ResponseWriter, message string, code int) { 
	err := Error { 
		Message: message,
		Code: code,
	}	

	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(code)
	rawBody, _ := json.Marshal(err)
	w.Write(rawBody)
}
