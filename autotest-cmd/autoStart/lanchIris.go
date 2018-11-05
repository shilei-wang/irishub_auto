package main

import (
	."github.com/irishub_auto/autotest-cmd/autoStart/utils"

	"fmt"
)

func main() {
	fmt.Println("(0) Get start ... ")
	//ForTest()

	Run_4_iris()
}

func Run_4_iris(){
	fmt.Println("(1) Delete 4 old files ... ")
	Params = []string{"iris1", "iris2", "iris3", "iris4", ".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	quit := make(chan bool)
	<-quit


	fmt.Println("(2) Iris init gen-tx * 4 ... ")
	Init_gentx()

	fmt.Println("(3) Get iris1 node name , SetNodeInfo()  ")
	if Err = SetNodeInfo(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

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


