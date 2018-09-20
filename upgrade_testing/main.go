package main

import (
	. "bianjie-qa/irishub/upgrade_testing/tools"
	"fmt"
)

func main() {
    fmt.Println("Let's get it start ......")

	Debug(" Step 1 ===> Delete old files ... ")
    Params = []string{"iris1","iris2","iris3","iris4",".iriscli"}
	if Err = Rm(Params); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	Debug(" Step 2 ===> Iris init gen-tx * 4 ... ")
	Init_gentx()

	Debug(" Step 3 ===> Get iris1 node name , Please check if the node name is ok ? ... ")
	if Err = SetNodeInfo(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	Debug(" Step 4 ===> Copy gentx json * 3 ... ")
	if Err = Copy_gentx(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	Debug(" Step 5 ===> Iris1 init gentxs, generate genesis.json and config.toml... ")
	Init_gentxs()

	Debug(" Step 6 ===> Iris1 copy Config * 3 ... ")
	if Err = CopyConfigGenesis(); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	Debug(" Step 7 ===> Write new Params to Toml * 3 ... ")
	if Err = WriteToml(Nodes[1]); Err != nil {
		fmt.Println(Err.Error())
		return
	}

	Debug(" Step 8 ===> Run 4 Iris ... ")
	Run_4_IRIS("old")

	Debug(" Step 9 ===> Check Block Chain status ... ")
	Print_iris_status()

	Debug(" Step 10 ===> Submit upgrade proposal ... ")
	SubmitProposal()

	Debug(" Step 11 ===> Vote proposal ... ")
	VoteProposal("1")

	Debug(" Step 12 ===> Inquiry Proposal until vote passed ... ")
	InquiryProposal("1")

	Debug(" Step 13 ===> Kill old iris processes *4 ... ")
	Kill_iris()

	Debug(" Step 14 ===> run new iris processes *4 ... ")
	Run_4_IRIS("new")

	Debug(" Step 15 ===> Submit switch * 4 ... ")
	SubmitSwitch("1")

	Debug(" Step 16 ===> Inquiry switch ... ")
	InquirySwitch()

	Debug(" Step 17 ===> Check new module... ")
	CheckNewModule()
}




