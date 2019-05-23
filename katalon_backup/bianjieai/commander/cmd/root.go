package cmd

import (
	"fmt"
	"github.com/gorilla/mux"
	"github.com/spf13/cobra"
	"net/http"
	"os"
	"github.com/spf13/viper"
	"log"
	"gitlab.bianjie.ai/bianjieai/commander/handler"
)

var rootCmd = &cobra.Command{
	Use:   "commander",
	Short: "Restful api for executing commands",
	RunE: func(cmd *cobra.Command, args []string) error {
		router := createRouter()

		log.Fatal(http.ListenAndServe(":8080", router))
		return nil
	},
}

func createRouter() http.Handler {
	r := mux.NewRouter()

	r.HandleFunc("/", handler.ExeSysCommand()).Methods("POST")
	return r
}

// Execute adds all child commands to the root command and sets flags appropriately.
// This is called by main.main(). It only needs to happen once to the rootCmd.
func Execute() {
	if err := rootCmd.Execute(); err != nil {
		fmt.Println(err)
		os.Exit(1)
	}
}

func init() {
	cobra.OnInitialize(initConfig)
}

// initConfig reads in config file and ENV variables if set.
func initConfig() {
	viper.AutomaticEnv() // read in environment variables that match
}
