package common

import (
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/requester"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/utils"
	. "gitlab.bianjie.ai/bianjieai/bianjie-qa/irishub/autotest-rest/types"

	"strconv"
	"errors"
	"time"
	"encoding/json"
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

	//注意：由于dev版本和stage版本获取version参数的方法不同
	versionResp, err := c.GetVersionOld()
	if err != nil {
		panic(ERR_FAUCET+err.Error())
	}
	r.Version = "v"+versionResp

	r.FaucetBalance = strconv.FormatFloat(amount, 'f', 5, 64) +" "+ DefaultCoinType
	r.ChainId = Config.Map["ChainID"]
	r.TimeNow = time.Now().Format("2006-01-02 15:04:05")
	return r
}

func (c *CommonWorker)DecodeAmount(s string) (float64, error) {
	s = strings.Replace(s,"iris","",-1)

	f,err := strconv.ParseFloat(s,64)
	if err != nil {
		//Debug(ERR_DECODE_AMOUNT)
		return 0, errors.New(ERR_DECODE_AMOUNT)
	}
	return f, nil
}

func (c *CommonWorker)CompareString(actual string, expected string) error {
	if actual != expected {
		return errors.New(MSG_ACTUAL + actual + MSG_EXPECTED + expected)
	}

	return nil
}

func (c *CommonWorker)CompareFloat64(actual float64, expected float64) error {
	if actual != expected {
		return errors.New(MSG_ACTUAL + strconv.FormatFloat(actual, 'f', 1, 64) + MSG_EXPECTED + strconv.FormatFloat(expected, 'f', 1, 64))
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
// 设置SID
func (c *CommonWorker)SetSID() error{
	//获取FAUCET SID
	account, err := c.getAccountInfoFromName(FAUCET)
	if err != nil {
		return errors.New(ERR_SETSID + err.Error())
	}

	FAUCET_SID, err = strconv.Atoi(account.Sequence)
	if err != nil {
		return errors.New(ERR_SETSID + err.Error())
	}

	//获取FAUCET_BANK SID
	account, err = c.getAccountInfoFromName(FAUCET_BANK)
	if err != nil {
		FAUCET_BANK_SID = 0
	} else {
		FAUCET_BANK_SID, err = strconv.Atoi(account.Sequence)
		if err != nil {
			return errors.New(ERR_SETSID + err.Error())
		}
	}

	//获取FAUCET_STAKE SID
	account, err = c.getAccountInfoFromName(FAUCET_STAKE)
	if err != nil {
		FAUCET_STAKE_SID = 0
	} else {
		FAUCET_STAKE_SID, err = strconv.Atoi(account.Sequence)
		if err != nil {
			return errors.New(ERR_SETSID + err.Error())
		}
	}

	//FAUCET_GOV SID
	account, err = c.getAccountInfoFromName(FAUCET_GOV)
	if err != nil {
		FAUCET_GOV_SID = 0
	} else {
		FAUCET_GOV_SID, err = strconv.Atoi(account.Sequence)
		if err != nil {
			return errors.New(ERR_SETSID + err.Error())
		}
	}

	//FAUCET_ACCOUNT SID
	account, err = c.getAccountInfoFromName(FAUCET_ACCOUNT)
	if err != nil {
		FAUCET_ACCOUNT_SID = 0
	} else {
		FAUCET_ACCOUNT_SID, err = strconv.Atoi(account.Sequence)
		if err != nil {
			return errors.New(ERR_SETSID + err.Error())
		}
	}

	//FAUCET_FEE SID
	account, err = c.getAccountInfoFromName(FAUCET_FEE)
	if err != nil {
		FAUCET_FEE_SID = 0
	} else {
		FAUCET_FEE_SID, err = strconv.Atoi(account.Sequence)
		if err != nil {
			return errors.New(ERR_SETSID + err.Error())
		}
	}

	return nil
}

// 读取SID， 并且+1 (这块如果faucet账户公用的话要加锁)
func (c *CommonWorker)GetSID(name string) string {
	var sid = -1

	switch {
	case name == FAUCET :
		sid = FAUCET_SID
		FAUCET_SID++

	case name == FAUCET_BANK :
		sid = FAUCET_BANK_SID
		FAUCET_BANK_SID++

	case name == FAUCET_STAKE :
		sid = FAUCET_STAKE_SID
		FAUCET_STAKE_SID++

	case name == FAUCET_GOV :
		sid = FAUCET_GOV_SID
		FAUCET_GOV_SID++

	case name == FAUCET_ACCOUNT :
		sid = FAUCET_ACCOUNT_SID
		FAUCET_ACCOUNT_SID++

	case name == FAUCET_FEE :
		sid = FAUCET_FEE_SID
		FAUCET_FEE_SID++
	}

	return strconv.Itoa(sid)
}

// 交易未成功，SID -1
func (c *CommonWorker)ResetSID(name string){
	switch {
	case name == FAUCET :
		FAUCET_SID--

	case name == FAUCET_BANK :
		FAUCET_BANK_SID--

	case name == FAUCET_STAKE :
		FAUCET_STAKE_SID--

	case name == FAUCET_GOV :
		FAUCET_GOV_SID--

	case name == FAUCET_ACCOUNT :
		FAUCET_ACCOUNT_SID--

	case name == FAUCET_FEE :
		FAUCET_FEE_SID--
	}
}

// 获取 Iris Version (升级加入后)
func (c *CommonWorker)GetVersion() (*VersionInfo, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/version",
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_GET_VERSION + ERR_REQUEST)
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_GET_VERSION + ERR_STATUSCODE)
	}

	repData := &VersionInfo{}
	err = json.Unmarshal(repBody, repData)
	if err != nil {
		return nil, errors.New(ERR_GET_VERSION + ERR_UNMARSHAL)
	}

	return repData, nil
}

// 获取 Iris Version
func (c *CommonWorker)GetVersionOld() (string, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/version",
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return "", errors.New(ERR_GET_VERSION + ERR_REQUEST)
	}
	if statusCode != 200 {
		return "", errors.New(ERR_GET_VERSION + ERR_STATUSCODE)
	}

	return string(repBody), nil
}


//keys - 显示用户信息
func (c *CommonWorker)ShowAccountInfo(name string) (*KeysAccountResp, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/keys/"+name,
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_SHOW_ACCOUNT_INFO + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_SHOW_ACCOUNT_INFO + ERR_STATUSCODE+ string(repBody))
	}

	repData := &KeysAccountResp{}
	err = json.Unmarshal(repBody, repData)
	if err != nil {
		return nil, errors.New(ERR_SHOW_ACCOUNT_INFO + ERR_UNMARSHAL)
	}

	return repData, nil
}

//keys - 删除所有用户
func (c *CommonWorker)KeysDeleteALL() error {
	repData, err := c.QueryAccountList()
	if err != nil {
		return errors.New(ERR_DELETE_ALL + err.Error())
	}

	for _, d := range *repData {
		if d.Name == "v1" || d.Name == "x1" {
			continue
		}

		err := c.DeleteAccount(d.Name, PASSWORD)
		if err != nil {
			return errors.New(ERR_DELETE_ALL + err.Error())
		}
	}

	return nil
}

//获取keys列表
func (c *CommonWorker)QueryAccountList() (*[]KeysAccountResp , error){
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/keys",
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_KEYLIST + ERR_REQUEST)
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_INQUIRY_KEYLIST + ERR_STATUSCODE)
	}

	repData := &[]KeysAccountResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_INQUIRY_KEYLIST+ ERR_UNMARSHAL)
	}

	return repData, nil
}

//根据用户名删除单个用户
func (c *CommonWorker)DeleteAccount(name string, passwd string) error {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/keys/"+name,
		"DELETE",
		nil,
	)
	resBody := DeleteAccountReq{Password: passwd}
	repBody, statusCode, err := c.RequestWorker.MakeRequest(resBody)
	if err != nil {
		return errors.New(ERR_DELETE_ACCOUNT + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return errors.New(ERR_DELETE_ACCOUNT + ERR_STATUSCODE + string(repBody))
	}

	return nil
}

//更新账户密码
func (c *CommonWorker)UpdateAccount(name string, newPassword string, oldPassword string) error {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/keys/"+name,
		"PUT",
		nil,
	)
	resBody := UpdateKeyReq{NewPassword : newPassword, OldPassword :oldPassword}
	repBody, statusCode, err := c.RequestWorker.MakeRequest(resBody)
	if err != nil {
		return errors.New(ERR_UPDATE_ACCOUNT + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return errors.New(ERR_UPDATE_ACCOUNT + ERR_STATUSCODE + string(repBody))
	}

	return nil
}

// 添加用户， seed为空 = 添加， seed不为空 = 恢复
func (c *CommonWorker)AddAccount(name string, passwd string ,seed string)(*AddAccountResp, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/keys",
		"POST",
		nil,
	)

	resBody := AddAccountReq{Name:name, Password:passwd, Seed:seed}
	repBody, statusCode, err := c.RequestWorker.MakeRequest(resBody)
	if err != nil {
		return nil, errors.New(ERR_ADD_ACCOUNT + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_ADD_ACCOUNT + ERR_STATUSCODE + string(repBody))
	}

	repData := &AddAccountResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_ADD_ACCOUNT + ERR_UNMARSHAL)
	}

	return repData, nil
}

//  交易转账， 从 srcName 转账 amount IRISs 到 dstName
func (c *CommonWorker)SendIris(srcName string, dstName string, amount string, data *SendIrisData) (*BroadcastTxResp, error) {
	dstResp, err := c.ShowAccountInfo(dstName)
	if err != nil {
		return nil, errors.New(ERR_SEND_IRIS + err.Error())
	}

	account, err := c.getAccountInfoFromName(srcName)
	if err != nil {
		return nil,errors.New(ERR_SEND_IRIS + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/bank/"+dstResp.Address+"/send",
		"POST",
		nil,
	)

	if data == nil{
		data = &SendIrisData{PASSWORD, c.GetSID(srcName),GasForSend, GasForFee}
	} else {
		if (*data).Password == DEFAULT { (*data).Password = PASSWORD }
		if (*data).Sequence == DEFAULT { (*data).Sequence = "0" }
		if (*data).Gas      == DEFAULT { (*data).Gas      = GasForSend }
		if (*data).Fee      == DEFAULT { (*data).Fee 	  = GasForFee }
	}

	sendData := SendIrisReq {
		Amount          :  amount+"iris",
		Sender          :  "",
		Basetx          :  BaseTx{
			Name            :  srcName,
			Password        :  (*data).Password,
			ChainId         :  Config.Map["ChainID"],
			AccountNumber   :  account.AccountNumber,
			Sequence        :  (*data).Sequence,
			Gas             :  (*data).Gas,
			Fee             :  (*data).Fee,
		},
	}

	//resBody , _ := json.Marshal(sendData)
	//fmt.Println(string(resBody))

	repBody, statusCode, err := c.RequestWorker.MakeRequest(sendData)
	if err != nil || repBody == nil {
		return nil, errors.New(ERR_SEND_IRIS + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_SEND_IRIS + ERR_STATUSCODE + string(repBody))
	}

	repData := &BroadcastTxResp{}
	err = json.Unmarshal(repBody, repData)
	if err != nil {
		return nil, errors.New(ERR_SEND_IRIS + ERR_STATUSCODE)
	}

	return repData, nil
}

//获得账户信息FromName (accountNumber, SequenceID, amount 等)
func (c *CommonWorker)getAccountInfoFromName(name string) (*AccountDataResp, error) {
	resp, err := c.ShowAccountInfo(name)
	if err != nil{
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMNAME + err.Error())
	}

	accountResp, err := c.GetAccountFromAddress(resp.Address)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMNAME + err.Error())
	}

	return accountResp, nil
}

//获得账户信息FromAddress (accountNumber, SequenceID, amount 等)
func (c *CommonWorker)GetAccountFromAddress(address string) (*AccountDataResp, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/bank/accounts/"+address,
		"GET",
		nil,
	)

	//不加--trust-node 会有问题 返回 cannot query
	//fmt.Println(Config.Map["HostIP"]+"/bank/accounts/"+address)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMADDRESS + ERR_REQUEST+string(repBody))
	}
	if statusCode != 200 {
		//fmt.Println(Config.Map["HostIP"]+"/bank/accounts/"+address)
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMADDRESS + ERR_STATUSCODE+strconv.Itoa(statusCode)+string(repBody))
	}

	repData := &AccountDataResp{}
	err = json.Unmarshal(repBody, repData)
	if err != nil {
		return nil, errors.New(ERR_GET_ACCOUNTINFO_FROMADDRESS + ERR_UNMARSHAL + err.Error())
	}

	return repData, nil
}

// 获取账户余额
func (c *CommonWorker)GetAccountIris(name string) (float64, error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return 0, errors.New(ERR_GET_ACCOUNTIRIS + err.Error())
	}

	amount, err := c.DecodeAmount(account.Coins[0])
	if err != nil {
		return 0, errors.New(ERR_GET_ACCOUNTIRIS + err.Error())
	}

	return amount, nil
}

//获取交易信息
func (c *CommonWorker)QueryTxInfo(txHash string) (*QueryTxResp, error) {
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/txs/"+txHash,
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_GET_TXINFO + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_GET_TXINFO + ERR_STATUSCODE+ string(repBody))
	}

	repData := &QueryTxResp{}
	err = json.Unmarshal(repBody, repData)
	if err != nil {
		return nil, errors.New(ERR_GET_TXINFO + ERR_UNMARSHAL)
	}

	return repData, nil
}

//获取验证人Validator列表
func (c *CommonWorker)QueryValidatorList() (*[]ValidatorResp , error){
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/stake/validators",
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_QUERY_VALIDATORLIST + ERR_REQUEST)
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_QUERY_VALIDATORLIST + ERR_STATUSCODE)
	}

	repData := &[]ValidatorResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_QUERY_VALIDATORLIST+ ERR_UNMARSHAL)
	}

	return repData, nil
}

//获取Delegation信息
func (c *CommonWorker)InquiryDelegation(delegator_name string, validator_add string) (*InquiryDelegationResp , error){
	account, err := c.getAccountInfoFromName(delegator_name)
	if err != nil {
		return nil,errors.New(ERR_INQUIRY_DELEGATION + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/stake/delegators/"+account.Address+"/delegations/"+validator_add,
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_DELEGATION + ERR_REQUEST)
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_INQUIRY_DELEGATION + ERR_STATUSCODE)
	}

	repData := &InquiryDelegationResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_INQUIRY_DELEGATION+ ERR_UNMARSHAL)
	}

	return repData, nil
}

//获取Delegation信息
func (c *CommonWorker)TxDelegation(amount string, delegator_name string, validator_address string)  (*BroadcastTxResp , error){
	account, err := c.getAccountInfoFromName(delegator_name)
	if err != nil {
		return nil,errors.New(ERR_TX_DELEGATION + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/stake/delegators/"+account.Address+"/delegations",
		"POST",
		nil,
	)

	delegationsInput := DelegationsInput {
		ValidatorAddr : validator_address,
		Delegation    : amount+"iris",
	}

	delegationsRes := DelegationsReq{
		Basetx          :  BaseTx{
			Name            :  delegator_name,
			Password        :  PASSWORD,
			ChainId         :  Config.Map["ChainID"],
			AccountNumber   :  account.AccountNumber,
			Sequence        :  c.GetSID(delegator_name),
			Gas             :  GasForSend,
			Fee             :  GasForFee,
		},
		Delegations : []DelegationsInput{delegationsInput},
		BeginUnbondings :    []BeginUnbondingInput{},
		CompleteUnbondings :  []CompleteUnbondingInput{},
		BeginRedelegates  :  []BeginRedelegateInput{},
		CompleteRedelegates : []CompleteRedelegateInput{},
	}

	//resBody , _ := json.Marshal(delegationsRes)
	//fmt.Println(string(resBody))

	repBody, statusCode, err := c.RequestWorker.MakeRequest(delegationsRes)
	if err != nil {
		return nil, errors.New(ERR_TX_DELEGATION + ERR_REQUEST + string(repBody) + err.Error())
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_TX_DELEGATION + ERR_STATUSCODE + string(repBody))
	}

	//注意 这里原来是数组
	repData := &BroadcastTxResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_TX_DELEGATION + ERR_UNMARSHAL + err.Error())
	}

	return repData, nil
}

//提交提议
func (c *CommonWorker)SubmitProposal(name string, title string, proposalType ProposalKind, depositAmount string)  (*BroadcastTxResp , error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return nil,errors.New(ERR_SUBMIT_PROPOSAL + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals",
		"POST",
		nil,
	)

	submitProposalRes := PostProposalReq {
		Basetx          :  BaseTx{
			Name            :  name,
			Password        :  PASSWORD,
			ChainId         :  Config.Map["ChainID"],
			AccountNumber   :  account.AccountNumber,
			Sequence        :  c.GetSID(name),
			Gas             :  GasForSend,
			Fee             :  GasForFee,
		},
		Title  			:        title,
		Description     :        "Description :submit Proposal",
		ProposalType    :   	 proposalType,
		Proposer        :		 account.Address,
		InitialDeposit  :        depositAmount+"iris",
	}

	//Params          :		 []Param{Param{
	//	Key:   "gov/depositprocedure/deposit",
	//	Value: "10000000000000000000iris",
	//	Op:    Update,
	//}},

	//resBody , _ := json.Marshal(submitProposalRes)
	//fmt.Println(string(resBody))

	repBody, statusCode, err := c.RequestWorker.MakeRequest(submitProposalRes)
	if err != nil {
		return nil, errors.New(ERR_SUBMIT_PROPOSAL + ERR_REQUEST + string(repBody) + err.Error())
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_SUBMIT_PROPOSAL + ERR_STATUSCODE + string(repBody))
	}

	//注意 这里是数组
	repData := &BroadcastTxResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_SUBMIT_PROPOSAL + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}

//查询提议列表
func (c *CommonWorker)QueryProposals() (*[]InquiryProposalResp , error){
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals",
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_QUERY_PROPOSALS + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_QUERY_PROPOSALS + ERR_STATUSCODE + string(repBody))
	}

	//注意 这里是数组
	repData := &[]InquiryProposalResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_QUERY_PROPOSALS + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}

//查询提议
func (c *CommonWorker)InquiryProposal(id string) (*InquiryProposalResp , error){
	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals/"+id,
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_PROPOSAL + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_INQUIRY_PROPOSAL + ERR_STATUSCODE + string(repBody))
	}

	repData := &InquiryProposalResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_INQUIRY_PROPOSAL + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}

//提交提议
func (c *CommonWorker)DepositProposal(name string, proposalID string, depositAmount string)  (*BroadcastTxResp , error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return nil,errors.New(ERR_DEPOSIT_PROPOSAL + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals/"+proposalID+"/deposits",
		"POST",
		nil,
	)

	depositReq := DepositReq  {
		Basetx           :  BaseTx{
			Name            :  name,
			Password        :  PASSWORD,
			ChainId         :  Config.Map["ChainID"],
			AccountNumber   :  account.AccountNumber,
			Sequence        :  c.GetSID(name),
			Gas             :  GasForSend,
			Fee             :  GasForFee,
		},
		Depositer		 :  account.Address,
		Amount   		 :  depositAmount+"iris",
	}

	repBody, statusCode, err := c.RequestWorker.MakeRequest(depositReq)
	if err != nil {
		return nil, errors.New(ERR_DEPOSIT_PROPOSAL + ERR_REQUEST + string(repBody) + err.Error())
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_DEPOSIT_PROPOSAL + ERR_STATUSCODE + string(repBody))
	}

	//注意 这里是数组
	repData := &BroadcastTxResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_DEPOSIT_PROPOSAL + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}

//投票提议
func (c *CommonWorker)VoteProposal(name string, proposalID string, voteOption string)  (*BroadcastTxResp , error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return nil, errors.New(ERR_VOTE_PROPOSAL + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals/"+proposalID+"/votes",
		"POST",
		nil,
	)

	voteReq := VoteReq  {
		Basetx          :  BaseTx{
			Name            :  name,
			Password        :  PASSWORD,
			ChainId         :  Config.Map["ChainID"],
			AccountNumber   :  account.AccountNumber,
			Sequence        :  c.GetSID(name),
			Gas             :  GasForSend,
			Fee             :  GasForFee,
		},
		Voter 	  :  account.Address,
		Option    :  voteOption,
	}

	repBody, statusCode, err := c.RequestWorker.MakeRequest(voteReq)
	if err != nil {
		return nil, errors.New(ERR_VOTE_PROPOSAL + ERR_REQUEST + string(repBody) + err.Error())
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_VOTE_PROPOSAL + ERR_STATUSCODE + string(repBody))
	}

	repData := &BroadcastTxResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_VOTE_PROPOSAL + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}

//查询投票
func (c *CommonWorker)InquiryVote(name string, proposalID string)  (*VoteResp , error){
	account, err := c.getAccountInfoFromName(name)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_VOTE + err.Error())
	}

	c.RequestWorker.Init(
		Config.Map["HostIP"]+"/gov/proposals/"+proposalID+"/votes/"+account.Address,
		"GET",
		nil,
	)

	repBody, statusCode, err := c.RequestWorker.MakeRequest(nil)
	if err != nil {
		return nil, errors.New(ERR_INQUIRY_VOTE + ERR_REQUEST + string(repBody))
	}
	if statusCode != 200 {
		return nil, errors.New(ERR_INQUIRY_VOTE + ERR_STATUSCODE + string(repBody))
	}

	repData := &VoteResp{}
	err = json.Unmarshal(repBody, &repData)
	if err != nil {
		return  nil, errors.New(ERR_INQUIRY_VOTE + ERR_UNMARSHAL+ err.Error())
	}

	return repData, nil
}



