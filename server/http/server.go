package http

import (
	"net/http"
	"github.com/gorilla/mux"
	"github.com/loginid1/ios-example/server/controllers/tokens"
	"github.com/loginid1/ios-example/server/controllers/utils"
)

func MakeServer() *http.Server { 
	r := mux.NewRouter()
	tk := r.PathPrefix("/token").Subrouter()
	tk.HandleFunc("", token.CreateToken).Methods("POST")
	tk.HandleFunc("/verify", token.VerifyToken).Methods("POST")

	ud := r.PathPrefix("/utils").Subrouter()
	ud.HandleFunc("/uuid", utils.CreateUUID).Methods("GET")

	srv := &http.Server{
		Addr: ":3000",
		Handler: r,
	}

	return srv
}
