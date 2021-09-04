package utils

import (
	"net/http"
	"encoding/json"
	"github.com/google/uuid"
)

type Response struct {
	UUID string `json:"uuid"`
}

func CreateUUID(w http.ResponseWriter, r *http.Request) { 
	uuidV4 := uuid.NewString()

	res := Response { 
		UUID: uuidV4,
	}
	
	w.Header().Add("Content-Type", "application/json")
	w.WriteHeader(200)
	rawBody, _ := json.Marshal(res)
	w.Write(rawBody)
}
