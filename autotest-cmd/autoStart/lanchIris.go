package main

import (
	. "github.com/irishub_auto/autotest-cmd/autoStart/utils"

	"fmt"
	"os"
)

func main() {
	fmt.Println("(0) Get start ... ")
	//ForTest()

	args := os.Args
	if args == nil || len(args) > 3 {
		fmt.Println("run nums??")
		return
	}

	num := args[1]
	BLOCK_TIME = "2"

	switch num {
		case "1":
			Run_testnets(num)
		case "2":
			Run_testnets(num)
		case "3":
			// TODO
		case "4":
			Run_testnets(num)
		case "c":
			Run_testnets_c(args[2])
		case "m":
			Run_testnets_m(args[2])
		case "t":
			Run_testnet_temp(args[2])


	default:
	}
}

func Run_testnets(num string){
	fmt.Println(".Run_"+num+"_testnets. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet ")
	Init_testnet(num)

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccount(num)

	fmt.Println("(6) Run "+num+" Iris ... ")
	StartAndPrint(num)

	quit := make(chan bool)
	<-quit
}

func Run_testnets_m(num string){
	fmt.Println(".Run_"+num+"_testnets. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet ")
	Init_testnet(num)

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis_m(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccount(num)

	fmt.Println("(6) Run "+num+" Iris ... ")
	StartAndPrint(num)

	quit := make(chan bool)
	<-quit
}

func Run_testnets_c(num string){
	fmt.Println(".Run_"+num+"_testnets. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet ")
	Init_testnet(num)

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis_c(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccount(num)

	fmt.Println("(6) Run "+num+" Iris ... ")
	StartAndPrint(num)

	quit := make(chan bool)
	<-quit
}

// cancel start
func Run_testnet_temp(num string){
	fmt.Println(".Run_"+num+"_testnets. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet ")
	Init_testnet(num)

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis_c(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	//BLOCK_TIME = "8"

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(num); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccount(num)


	//time.Sleep(time.Duration(30)*time.Second)
	//
	//fmt.Println("(6) Run "+num+" Iris ... ")
	//StartAndPrint(num)
	//
	//quit := make(chan bool)
	//<-quit
}
