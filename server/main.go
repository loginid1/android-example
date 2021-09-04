package main

import (
	"github.com/loginid1/ios-example/server/http"
	"github.com/joho/godotenv"
)

func server() {
	srv := http.MakeServer()
	srv.ListenAndServe()
}

func main() { 
	godotenv.Load(".env")
	server()
}
