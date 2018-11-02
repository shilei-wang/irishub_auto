
package autotest_cmd

import (
	"testing"
	. "github.com/irishub_auto/autotest-cmd/utils"
	"github.com/irishub_auto/autotest-cmd/stories"
	//. "github.com/irishub_auto/autotest-cmd/common"

	"fmt"
)

func Test_Module_ID_0_initialize(t *testing.T) {
	fmt.Println(" == Test_Module_ID_0_initialize == ")
	Config.Init()
	//Faucet.Init()
}

func Test_Module_ID_1_DefinitionStressTest(t *testing.T) {
	if Config.Map["DefinitionStressTest_Skip"]   == "true" { return }

	fmt.Println(" == Test_Module_ID_1_DefinitionStressTest == ")
	(&stories.DefinitionStress{}).Run()

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

	//(&stories.IparamFunction{}).Run()                   // Story 8-1 : IparamFunction 测试
}

func Test_Module_ID_10_finalize(t *testing.T) {
	fmt.Println(" == Test_Module_ID_10_finalize == ")
	//Logger.GenerateHTMLReport((&CommonWorker{}).GetReportData())
}

