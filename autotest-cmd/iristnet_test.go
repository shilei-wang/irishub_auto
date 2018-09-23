/*
iris init gen-tx --name=faucet --home=c:\.iris
iris init --gen-txs --chain-id=upgrade-test -o --home=c:\.iris
改genesis
iris start --home=c:\.iris

at home ====>
iris unsafe_reset_all --home=c:\.iris && iris start --home=c:\.iris

spread runway reform dream market profit coil session material arrive electric margin urban pulse segment injury ski december virtual suggest wish sister bounce satisfy
faa170639cm92ts6gnrzuu8wysye05k66l4f9tkfje

iriscli bank account faa1tf5y7ejehz66lnv9jx04g23slxpqty3wa8pwvh --node=http://192.168.199.222:26657 --trust-node
iriscli bank account faa170639cm92ts6gnrzuu8wysye05k66l4f9tkfje --node=http://localhost:26657 --trust-node
iriscli bank send --amount=1iris --fee=0.004iris  --chain-id=upgrade-test --from=faucet --to=faa1tf5y7ejehz66lnv9jx04g23slxpqty3wa8pwvh --node=http://localhost:26657

*/

package autotest_cmd

import (
	"testing"
	. "bianjie-qa/irishub/autotest-cmd/utils"
	. "bianjie-qa/irishub/autotest-cmd/common"
	"bianjie-qa/irishub/autotest-cmd/stories"

	"fmt"
)

func Test_Module_ID_0_initialize(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_0_initialize == ")
	Config.Init()
	Faucet.Init()
}

func Test_Module_ID_1_Keys(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_1_Keys == ")
	if Config.Map["Keys_Skip"]   == "true" { return }
}

func Test_Module_ID_2_Bank(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_2_Bank == ")
	if Config.Map["Bank_Skip"]   == "true" { return }
}

func Test_Module_ID_3_Stake(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_3_Stake == ")
	if Config.Map["Stake_Skip"]  == "true" { return }
}

func Test_Module_ID_4_Goverance(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_4_Goverance == ")
	if Config.Map["Gov_Skip"]    == "true" { return }
}

func Test_Module_ID_5_Account(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_5_Account == ")
	if Config.Map["Account_Skip"]== "true" { return }
}

func Test_Module_ID_6_Fee(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_6_Fee == ")
	if Config.Map["Fee_Skip"]== "true" { return }
}

func Test_Module_ID_8_Iparam(t *testing.T) {
	//fmt.Println(" == Test_Module_ID_6_Fee == ")
	if Config.Map["Iparam_Skip"]== "true" { return }

	(&stories.IparamFunction{}).Run()                   // Story 8-1 : IparamFunction 测试
}

func Test_Module_ID_10_finalize(t *testing.T) {
	fmt.Println(" == Test_Module_ID_10_finalize == ")
	Logger.GenerateHTMLReport((&CommonWorker{}).GetReportData())
}

