package common

import (
	. "github.com/irishub_auto/autotest-cmd/requester"
	. "github.com/irishub_auto/autotest-cmd/utils"
	. "github.com/irishub_auto/autotest-cmd/types"

	"strconv"
	"errors"
	"time"
	"strings"
	"fmt"
	"encoding/json"
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

	versionResp, err := c.GetVersion()
	if err != nil {
		panic(ERR_FAUCET+err.Error())
	}
	r.Version = "v"+versionResp

	r.FaucetBalance = strconv.FormatFloat(amount, 'f', 0, 64) +" "+ DefaultCoinType
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
	fmt.Println("")

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

/* 记得删除
params = append(params, "--from=v1")
params = append(params, "--to=faa1xxz6l2ddldha4k6kkaq4cfpz05hzsfnsnch6mj")
params = append(params, TXAMOUNT)
params = append(params, TXFEE)
params = append(params, CHAINID)
params = append(params, NODE)
params = append(params, JSON)
params = append(params, TRUSTNODE)

inputs := []string{PASSWORD}
*/

// 获取 Iris Version
func (c *CommonWorker)GetVersion() (string, error) {
	params := []string{"version"}

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return "", errors.New(ERR_GET_VERSION + err.Error() + string(repBody))
	}

	return repBody, nil
}

//keys - 显示用户信息
func (c *CommonWorker)ShowAccountInfo(name string) (string, error) {
	params := []string{"keys","show", name}

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return "", errors.New(ERR_SHOW_ACCOUNT_INFO + err.Error() + string(repBody))
	}

	var resp string
	if resp, err = ParseAddress(repBody); err != nil {
		return "", errors.New(ERR_SHOW_ACCOUNT_INFO + err.Error())
	}

	return resp, nil
}

// 添加用户， seed为空 = 添加， seed不为空 = 恢复
func (c *CommonWorker)AddAccount(name string, passwd string, seed string) (string, error) {
	params := []string{"keys", "add", name}
	inputs := []string{PASSWORD}
	if seed != "" {
		params = append(params, "--recover")
		inputs = append(inputs, seed)
	}

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, inputs)
	if err != nil {
		return "", errors.New(ERR_ADD_ACCOUNT + err.Error())
	}

	if !strings.Contains(repBody, "NAME:") {
		return "", errors.New(ERR_ADD_ACCOUNT)
	}

	//Add暂时不需要返回数据，只需要返回是否成功
	return "", nil
}

//  交易转账， 从 srcName 转账 amount IRISs 到 dstName
// iriscli bank send --from=faucet --amount=1iris --fee=0.004iris --chain-id=upgrade-test --to=faa1tf5y7ejehz66lnv9jx04g23slxpqty3wa8pwvh --node=http://192.168.150.239:26657
func (c *CommonWorker)SendIris(srcName string, dstName string, amount string, data *SendIrisData) (string, error){
	dstAddress, err := c.ShowAccountInfo(dstName)
	if err != nil {
		return "", errors.New(ERR_SEND_IRIS + err.Error())
	}

	params := []string{"bank", "send"}
	params = append(params, "--from="+srcName)
	params = append(params, "--to="+dstAddress)
	params = append(params, CHAINID)
	params = append(params, NODE)
	params = append(params, JSON)
	if data == nil{
		params = append(params, TXGAS)
		params = append(params, TXFEE)
		if amount == DEFAULT { params = append(params, TXAMOUNT)} else {params = append(params, "--amount="+amount)}
	} else {
		//if (*data).Gas      == DEFAULT { (*data).Gas    = GasForSend }
		//if (*data).Fee      == DEFAULT { (*data).Fee 	= GasForFee }
	}
	inputs := []string{PASSWORD}

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, inputs)
	if err != nil {
		return "", errors.New(ERR_SEND_IRIS + err.Error())
	}

	//fmt.Println(GetJson(repBody))
	repData := &BroadcastTxResp{}
	err = json.Unmarshal([]byte(GetJson(repBody)), repData)
	if err != nil {
		return "", errors.New(ERR_SEND_IRIS + ERR_UNMARSHAL+err.Error())
	}

	//返回哈希
	return repData.Hash, nil
}

//根据用户名删除单个用户
func (c *CommonWorker)DeleteAccount(name string, passwd string) error {
	params := []string{"keys", "delete", name}
	inputs := []string{PASSWORD}

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, inputs)
	if err != nil {
		return errors.New(ERR_DELETE_ACCOUNT + err.Error())
	}

	if !strings.Contains(repBody, "Password deleted forever") {
		return errors.New(ERR_DELETE_ACCOUNT)
	}

	return nil
}

//keys - 删除所有用户
func (c *CommonWorker)KeysDeleteALL() error {
	params := []string{"keys", "list"}

	repBody, err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return errors.New(ERR_DELETE_ALL + err.Error())
	}

	accounts := ParseAccounts(repBody)
	for _, account := range accounts{
		if err = c.DeleteAccount(account,PASSWORD); err != nil {
			return errors.New(ERR_DELETE_ALL + err.Error())
		}
	}

	return nil
}

// 获取账户余额
func (c *CommonWorker)GetAccountIris(name string) (float64, error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return 0, errors.New(ERR_GET_ACCOUNTIRIS + err.Error())
	}

	amount, err := DecodeAmount(account.Coins[0])
	if err != nil {
		return 0, errors.New(ERR_GET_ACCOUNTIRIS + err.Error())
	}

	return amount, nil
}

//获得账户信息FromName (accountNumber, SequenceID, amount 等)
func (c *CommonWorker)getAccountInfoFromName(name string) (*AccountDataResp, error) {
	respAddress, err := c.ShowAccountInfo(name)
	if err != nil{
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMNAME + err.Error())
	}

	accountResp, err := c.GetAccountFromAddress(respAddress)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMNAME + err.Error())
	}

	return accountResp, nil
}

//获得账户信息FromAddress (accountNumber, SequenceID, amount 等)
//iriscli bank account faa170639cm92ts6gnrzuu8wysye05k66l4f9tkfje --node=http://localhost:26657 --trust-node
func (c *CommonWorker)GetAccountFromAddress(address string) (*AccountDataResp, error) {
	params := []string{"bank", "account"}
	params = append(params, address)
	params = append(params, NODE)
	params = append(params, TRUSTNODE)

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMADDRESS + err.Error())
	}

	repData := &AccountDataResp{}
	err = json.Unmarshal([]byte(GetJson(repBody)), repData)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMADDRESS + ERR_UNMARSHAL+err.Error())
	}

	return repData, nil
}

//获取验证人Gov-iparam-module列表
//iriscli gov query-params  --node=http://localhost:26657 --trust-node --module=gov
func (c *CommonWorker)QueryIparamModule(module string) (*GovModuleResp , error){
	params := []string{"gov","query-params"}
	params = append(params, "--module="+module)
	params = append(params, NODE)
	params = append(params, TRUSTNODE)

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_IPARAMMODULE + err.Error() + string(repBody))
	}

	repData := &GovModuleResp{}
	err = json.Unmarshal([]byte(repBody), repData)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_IPARAMMODULE + ERR_UNMARSHAL+err.Error())
	}

	return repData, nil
}

//获取验证人Gov-iparam-key列表
//iriscli gov query-params --key=Gov/gov/DepositProcedure --node=http://192.168.199.222:26657 --trust-node
func (c *CommonWorker)QueryIparamKey(module string) (*GovKeyResp , error){
	params := []string{"gov","query-params"}
	params = append(params, "--key="+module)
	params = append(params, NODE)
	params = append(params, TRUSTNODE)

	repBody,  err := c.RequestWorker.MakeRequest(COMMAND_CLI, params, nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_IPARAMKEY + err.Error() + string(repBody))
	}

	//验证这里开始 需要gov的windows版本
	//fmt.Println(repBody)

	repData := &GovKeyResp{}
	err = json.Unmarshal([]byte(repBody), repData)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_IPARAMKEY + ERR_UNMARSHAL+err.Error())
	}

	return repData, nil
}

/**********************
**********************/

func (c *CommonWorker)DefineDifinition(serviceName string, fileName string, name string) {
	params := []string{"iservice","define"}
	params = append(params, "--from="+name)
	params = append(params, "--name="+serviceName)
	params = append(params, "--file="+fileName)
	params = append(params, CHAINID)
	params = append(params, "--fee=1iris")
	params = append(params, "--gas=10000000")
	params = append(params, "--service-description=service-description")
	params = append(params, "--author-description=author-description")
	params = append(params, "--broadcast=Broadcast")
	params = append(params, "--tags=tag1 tag2")

	//fmt.Print(params)

	inputs := []string{PASSWORD}

	c.RequestWorker.ExecNoStdout(COMMAND_CLI, params, inputs)
}










//===================================================
//===================================================
//===================================================

//获取keys列表
func (c *CommonWorker)QueryAccountList() {}

//更新账户密码
func (c *CommonWorker)UpdateAccount(){}

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

