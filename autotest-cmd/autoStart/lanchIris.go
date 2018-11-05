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
			Run_1_iris()
		case "4":
			Run_4_iris()
		default:
	}

}

func Run_1_iris(){
	fmt.Println(".Run_1_iris. ")
	fmt.Println("(1) Delete 4 old files ... ")
	Params = []string{"iris1", "iris2", "iris3", "iris4", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) Iris init gen-tx * 1 ... ")
	Init_gentx(1)

	fmt.Println("(3) ModifyGenesis in iris1... ")
	if Err = ModifyGenesis(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(4) Run 1 Iris ... ")
	StartAndPrint(1)

	quit := make(chan bool)
	<-quit
}



func Run_4_iris(){
	fmt.Println("(1) Delete 4 old files ... ")
	Params = []string{"iris1", "iris2", "iris3", "iris4", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(2) Iris init gen-tx * 4 ... ")
	Init_gentx(4)

	//https://github.com/irisnet/irishub/blob/feature/deps/docs/zh/get-started/Genesis-Generation-Process.md
	//wait!!!!

	fmt.Println("(3) Get iris1 node name , SetNodeInfo()  ")
	if Err = SetNodeInfo(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	return


	fmt.Println("(4) Copy 3 gentx Dirs(iris2,iris3,iris4) to iris1 ... ")
	if Err = Copy_gentx(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(5) Iris1 init gentxs, generate genesis.json ... ")
	Init_gentxs()


	fmt.Println("(6) ModifyGenesis in iris1... ")
	if Err = ModifyGenesis(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(7) Copy iris1-genesis.json to iris2,iris3,iris4 ")
	if Err = CopyGenesis(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(8) Modify config.toml in iris2,iris3,iris4 .... ")
	if Err = ModifyToml(Nodes[1]); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	fmt.Println("(9) Run 4 Iris ... ")
	StartAndPrint(3)

	quit := make(chan bool)
	<-quit
}

func ResetAndRun_4_iris(){

}


