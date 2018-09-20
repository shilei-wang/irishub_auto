package common

import (
	. "bianjie-qa/irishub/autotest-cmd/requester"
	. "bianjie-qa/irishub/autotest-cmd/utils"
	. "bianjie-qa/irishub/autotest-cmd/types"

	"strconv"
	"errors"
	"time"
	"strings"
)

type CommonWorker struct {
	RequestWorker Requester
}

func (c *CommonWorker)Init(){
	c.RequestWorker = Requester{}
}

func (c *CommonWorker)GetReportData() ReportData {
	r := ReportData{}

	// 获取水龙头余额
	amount , err := c.GetAccountIris(FAUCET)
	if err != nil{
		panic(ERR_GET_REPORTDATA+err.Error())
	}

	//versionResp, err := c.GetVersion()
	//if err != nil {
	//	panic(ERR_FAUCET+err.Error())
	//}
	//r.Version = "v"+versionResp.IrisVersion

	//注意：由于dev版本和stage版本获取version参数的方法不同， 暂时先hardcode版本信息， 后续接口稳定会切换上述方法获取version
	r.Version = "v0.4.1"

	r.FaucetBalance = strconv.FormatInt(amount,10)
	r.ChainId = Config.Map["ChainID"]
	r.TimeNow = time.Now().Format("2006-01-02 15:04:05")
	return r
}

func (c *CommonWorker)CompareString(actual string, expected string) error {
	if actual != expected {
		return errors.New(MSG_ACTUAL + actual + MSG_EXPECTED + expected)
	}

	return nil
}

func (c *CommonWorker)CompareInt64(actual int64, expected int64) error {
	if actual != expected {
		return errors.New(MSG_ACTUAL + strconv.FormatInt(actual,10) + MSG_EXPECTED + strconv.FormatInt(expected,10))
	}

	return nil
}

func (c *CommonWorker)StringContains(actual string, expected string) error {
	if !strings.Contains(actual, expected) {
		return errors.New(MSG_ACTUAL + actual + MSG_EXPECTED + expected)
	}

	return nil
}



/**************************
	主要操作
**************************/

// 获取 Iris Version
func (c *CommonWorker)GetVersion() {}

//keys - 显示用户信息
func (c *CommonWorker)ShowAccountInfo(t string) ([]byte, error) {return nil,nil}

//keys - 删除所有用户
func (c *CommonWorker)KeysDeleteALL() error {return nil}

//获取keys列表
func (c *CommonWorker)QueryAccountList() {}

//根据用户名删除单个用户
func (c *CommonWorker)DeleteAccount(t string) error {return nil}

//更新账户密码
func (c *CommonWorker)UpdateAccount(){}

// 添加用户， seed为空 = 添加， seed不为空 = 恢复
func (c *CommonWorker)AddAccount(t string)([]byte, error) {return nil,nil}

//  交易转账， 从 srcName 转账 amount IRISs 到 dstName
func (c *CommonWorker)SendIris(t string)([]byte, error) {return nil,nil}

//获得账户信息FromName (accountNumber, SequenceID, amount 等)
func (c *CommonWorker)getAccountInfoFromName(){}

//获得账户信息FromAddress (accountNumber, SequenceID, amount 等)
func (c *CommonWorker)GetAccountFromAddress(){}

// 获取账户余额
func (c *CommonWorker)GetAccountIris(t string)(int64, error){return 0,nil}

//获取交易信息
func (c *CommonWorker)QueryTxInfo(){}

//获取验证人Validator列表
func (c *CommonWorker)QueryValidatorList(){}

//获取Delegation信息
func (c *CommonWorker)InquiryDelegation(){}

//获取Delegation信息
func (c *CommonWorker)TxDelegation(){}

//提交提议
func (c *CommonWorker)SubmitProposal(){}

//查询提议列表
func (c *CommonWorker)QueryProposals() {}

//查询提议
func (c *CommonWorker)InquiryProposal(){}

//提交提议
func (c *CommonWorker)DepositProposal(){}

//投票提议
func (c *CommonWorker)VoteProposal(){}

//查询投票
func (c *CommonWorker)InquiryVote(){}



