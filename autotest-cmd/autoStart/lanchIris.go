package main

import (
	."github.com/irishub_auto/autotest-cmd/autoStart/utils"

	"fmt"
	"os"
)

func main() {
	fmt.Println("(0) Get start ... ")
	//ForTest()

	args := os.Args
	if args == nil || len(args) !=2 {
		fmt.Println("run nums??")
		return
	}

	num := args[1]
	switch num {
		case "1":
			Run_testnet_1()
		case "4":
			Run_testnet_4()
		case "r":
			Run_testnet_r()
		default:
	}
}


func Run_testnet_1(){
	fmt.Println(".Run_1_iris. ")
	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet --v=1 ... ")
	Init_testnet("1")

	fmt.Println("(3) ModifyGenesis in iris1... ")
	if Err = ModifyGenesis(1); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Add account for v0 .... ")
	AddAccount(1)

	fmt.Println("(5) Run 1 Iris ... ")
	StartAndPrint(1)

	quit := make(chan bool)
	<-quit
}

func Run_testnet_4(){
	fmt.Println(".Run_4_iris. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet --v=4 ... ")
	Init_testnet("4")

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis(4); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccount(4)

	fmt.Println("(6) Run 4 Iris ... ")
	StartAndPrint(4)

	quit := make(chan bool)
	<-quit
}

func Run_testnet_r(){
	fmt.Println(".Run_4_iris. ")

	fmt.Println("(1) Delete old files ... ")
	Params = []string{"testnet", ".iriscli", ".irislcd"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) iris testnet --v=4 ... ")
	Init_testnet("4")

	fmt.Println("(3) ModifyGenesis in v0,v1,v2,v3.. ")
	if Err = ModifyGenesis(4); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Modify config.toml in v1,v2,v3 .... ")
	if Err = ModifyToml(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Add account for v0,v1,v2,v3 .... ")
	AddAccountForRest()

	fmt.Println("(6) Run 4 Iris ... ")
	StartAndPrint(4)

	quit := make(chan bool)
	<-quit
}
