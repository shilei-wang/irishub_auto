package autotest_cmd

import (
	"testing"
	. "bianjie-qa/irishub/autotest-cmd/utils"
	"bianjie-qa/irishub/autotest-cmd/stories"

	"fmt"
)

func Test_Module_ID_0_initialize(t *testing.T) {
	fmt.Println(" == Test_Module_ID_0_initialize == ")
	//Config.Init()
	//Faucet.Init()
}

func Test_Module_ID_1_Keys(t *testing.T) {
	fmt.Println(" == Test_Module_ID_1_Keys == ")
	if Config.Map["Keys_Skip"]   == "true" { return }

	//(&stories.QueryAccountList{}).Run()      // Story 1-1 : 查询账户列表
}

func Test_Module_ID_2_Bank(t *testing.T) {
	fmt.Println(" == Test_Module_ID_2_Bank == ")

	(&stories.AccountTransfer{}).Run()       // Story 2-1 : 转账交易

	if Config.Map["Bank_Skip"]   == "true" { return }
}

func Test_Module_ID_3_Stake(t *testing.T) {
	fmt.Println(" == Test_Module_ID_3_Stake == ")
	if Config.Map["Stake_Skip"]  == "true" { return }
}

func Test_Module_ID_4_Goverance(t *testing.T) {
	fmt.Println(" == Test_Module_ID_4_Goverance == ")
	if Config.Map["Gov_Skip"]    == "true" { return }
}

func Test_Module_ID_5_Account(t *testing.T) {
	fmt.Println(" == Test_Module_ID_5_Account == ")
	if Config.Map["Account_Skip"]== "true" { return }
}

func Test_Module_ID_6_Fee(t *testing.T) {
	fmt.Println(" == Test_Module_ID_6_Fee == ")
	if Config.Map["Fee_Skip"]== "true" { return }
}

func Test_Module_ID_10_finalize(t *testing.T) {
	fmt.Println(" == Test_Module_ID_10_finalize == ")
//	Logger.GenerateHTMLReport((&CommonWorker{}).GetReportData())
}

