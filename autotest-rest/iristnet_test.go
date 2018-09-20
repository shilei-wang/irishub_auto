package autotest_rest

import (
	"testing"
	. "bianjie-qa/irishub/autotest-rest/types"
	. "bianjie-qa/irishub/autotest-rest/utils"
	. "bianjie-qa/irishub/autotest-rest/common"

	"bianjie-qa/irishub/autotest-rest/stories"
)

func initialize(){
	Config.Init()
	Faucet.Init()
	Wg.Add(NUMBER_OF_MODULES)
}

func finalize(){
	Wg.Wait()
	Logger.GenerateHTMLReport((&CommonWorker{}).GetReportData())
}

func Test_Module_ID_0_Start(t *testing.T) {
	initialize()
	t.Parallel()
	finalize()
}

func Test_Module_ID_1_Keys(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Keys_Skip"]   == "true" { return }

	(&stories.QueryAccountList{}).Run()      // Story 1-1 : 查询账户列表
	(&stories.CreateAccount{}).Run()         // Story 1-2 : 创建本地账户
	(&stories.RecoverAccount{}).Run()        // Story 1-3 : 恢复本地账户
	(&stories.InquiryAccountInfo{}).Run()    // Story 1-4 : 查看本地账户
	(&stories.DeleteAccount{}).Run()         // Story 1-5 : 删除本地账户
	(&stories.UpdateAccount{}).Run()         // Story 1-6 : 更新账户密码
}

func Test_Module_ID_2_Bank(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Bank_Skip"]   == "true" { return }

	(&stories.AccountTransfer{}).Run()       // Story 2-1 : 转账交易
	(&stories.InquiryTxInfo{}).Run()         // Story 2-2 : 查询交易
}

func Test_Module_ID_3_Stake(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Stake_Skip"]  == "true" { return }

	(&stories.QueryValidators{}).Run()       // Story 3-1 : 查询验证人列表
	(&stories.InquiryDelegation{}).Run()     // Story 3-4 : 查询委托信息
	(&stories.TxDelegation{}).Run()          // Story 3-7 : 委托交易
}

func Test_Module_ID_4_Goverance(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Gov_Skip"]    == "true" { return }

	(&stories.SubmitProposal{}).Run()        // Story 4-1 : 提交提议
	(&stories.InquiryProposal{}).Run()       // Story 4-2 : 查询提议
	(&stories.DepositProposal{}).Run()       // Story 4-3 : 赞助提议
	(&stories.VoteProposal{}).Run()          // Story 4-4 : 投票提议
	(&stories.InquiryVote{}).Run()           // Story 4-5 : 查询投票
}

func Test_Module_ID_5_Account(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Account_Skip"]== "true" { return }

	(&stories.AccountStatus{}).Run()         // Story 5-1 : 账户状态查询
}

func Test_Module_ID_6_Fee(t *testing.T) {
	t.Parallel()
	defer Wg.Done()
	if Config.Map["Fee_Skip"]== "true" { return }

	(&stories.Fee{}).Run()                   // Story 6-1 : Fee功能测试
}

