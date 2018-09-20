package types

// 添加用户
type AddAccountReq struct {
	Name     		  string 		  `json:"name"`
	Password          string 		  `json:"password"`
	Seed              string  		  `json:"seed"`
}

type AddAccountResp struct {
	Name     		  string  		  `json:"name"`
	Type     		  string  		  `json:"type"`
	Address           string  		  `json:"address"`
	Pub_key           string  		  `json:"pub_key"`
	Seed         	  string  		  `json:"seed"`
}

// keys 查询用户
type KeysAccountResp struct {
	Name  			  string
	Type              string
	Address 		  string
	Pub_key 	      string
}

// 查询account数据
type AccountDataResp struct {
	Address       	  string    	`json:"address"`
	Coins         	  []string     	`json:"coins"`
	PubKey        	  PubKey     	`json:"public_key"`
	AccountNumber 	  string     	`json:"account_number"`
	Sequence      	  string      	`json:"sequence"`
}

type PubKey interface {}

//[要删]
type Coin struct {
	Amount  string
}

// 删除账户
type DeleteAccountReq struct {
	Password          string        `json:"password"`
}

// 更新账户密码
type UpdateKeyReq struct { //UpdateKeyBody
	NewPassword string `json:"new_password"`
	OldPassword string `json:"old_password"`
}

// 转账交易

//转账Res
type SendIrisReq struct {
	Amount            []AmountItem  `json:"amount"`
	Sender			  string        `json:"sender"`
	Basetx			  BaseTx        `json:"base_tx"`
}

type AmountItem struct {
	Amount            string        `json:"amount"`
	Denom             string  	    `json:"denom"`
}

type BaseTx struct {
	Name              string  		`json:"name"`
	Password          string   		`json:"password"`
	ChainId           string   		`json:"chain_id"`
	AccountNumber     string   		`json:"account_number"`
	Sequence          string      	`json:"sequence"`
	Gas               string    	`json:"gas"`
	Fee               string        `json:"fee"`
}

type SendIrisData struct {
	Password        string
	Sequence        string
	Gas             string
	Fee             string
}

//转账Resp
// 所有的交易类型都是这个返回
type BroadcastTxResp struct { //ResultBroadcastTxCommit
	CheckTx   ResponseCheckTx          `json:"check_tx"`
	DeliverTx ResponseDeliverTx        `json:"deliver_tx"`
	Hash      string	               `json:"hash"`
	Height    string                   `json:"height"`
}

type KVPair struct {
	// 先要unmarshal成[]byte， 再string() 才可以读出来 ，相当于解码2次
	Key              []byte  		`protobuf:"bytes,1,opt,name=key,proto3" json:"key,omitempty"`
	Value			 []byte    		`protobuf:"bytes,2,opt,name=value,proto3" json:"value,omitempty"`
}

type ResponseCheckTx struct {
	Log       string          `protobuf:"bytes,3,opt,name=log,proto3" json:"log,omitempty"`
	GasWanted string          `protobuf:"varint,5,opt,name=gasWanted,json=gasWanted,proto3" json:"gasWanted,omitempty"`
	GasUsed   string          `protobuf:"varint,6,opt,name=gas_used,json=gasUsed,proto3" json:"gasUsed,omitempty"`
	Tags      []KVPair 	 	  `protobuf:"bytes,7,rep,name=tags" json:"tags,omitempty"`

	Code      uint32          `protobuf:"varint,1,opt,name=code,proto3" json:"code,omitempty"`
	Data      []byte          `protobuf:"bytes,2,opt,name=data,proto3" json:"data,omitempty"`
	Info      string          `protobuf:"bytes,4,opt,name=info,proto3" json:"info,omitempty"`
}

type ResponseDeliverTx struct {
	Log       string          `protobuf:"bytes,3,opt,name=log,proto3" json:"log,omitempty"`
	GasWanted string          `protobuf:"varint,5,opt,name=gasWanted,json=gasWanted,proto3" json:"gasWanted,omitempty"`
	GasUsed   string          `protobuf:"varint,6,opt,name=gas_used,json=gasUsed,proto3" json:"gasUsed,omitempty"`
	Tags      []KVPair 	 	  `protobuf:"bytes,7,rep,name=tags" json:"tags,omitempty"`

	Code      uint32          `protobuf:"varint,1,opt,name=code,proto3" json:"code,omitempty"`
	Data      []byte          `protobuf:"bytes,2,opt,name=data,proto3" json:"data,omitempty"`
	Info      string          `protobuf:"bytes,4,opt,name=info,proto3" json:"info,omitempty"`
}

// 查询交易
type QueryTxResp struct { //txInfo
	Hash   string                  `json:"hash"`
	Height string                  `json:"height"`
	Tx     interface{}             `json:"tx"`
	Result ResponseDeliverTx       `json:"result"`
}

// 验证人Validator
type ValidatorResp struct { //BechValidator
	Owner   string		 			`json:"owner"`   // in bech32 , sdk.AccAddress
	PubKey  string		 			`json:"pub_key"` // in bech32
	Revoked bool  		 			`json:"revoked"` // has the validator been revoked from bonded status?

	Status           byte		    `json:"status"`           // validator status (bonded/unbonding/unbonded), sdk.BondStatus
	Tokens           string 		`json:"tokens"`           // delegated tokens (incl. self-delegation), sdk.Rat
	DelegatorShares  string 		`json:"delegator_shares"` // total shares issued to a validator's delegators,  sdk.Rat

	Description        Description `json:"description"`           // description terms for the validator
	BondHeight         string      `json:"bond_height"`           // earliest height as a bonded validator, int64
	BondIntraTxCounter int16       `json:"bond_intra_tx_counter"` // block-local tx index of validator change
	ProposerRewardPool []Coin      `json:"proposer_reward_pool"`  // XXX reward pool collected from being the proposer

	Commission            string   `json:"commission"`              // XXX the commission rate of fees charged to any delegators, sdk.Rat
	CommissionMax         string   `json:"commission_max"`          // XXX maximum commission rate which this validator can ever charge, sdk.Rat
	CommissionChangeRate  string   `json:"commission_change_rate"`  // XXX maximum daily increase of the validator commission, sdk.Rat
	CommissionChangeToday string   `json:"commission_change_today"` // XXX commission rate change today, reset each day (UTC time), sdk.Rat

	// fee related
	LastBondedTokens      string   `json:"prev_bonded_shares"` // last bonded token amount,sdk.Rat
}

type Description struct {
	Moniker  string `json:"moniker"`  // name
	Identity string `json:"identity"` // optional identity signature (ex. UPort or Keybase)
	Website  string `json:"website"`  // optional website link
	Details  string `json:"details"`  // optional details
}

//查询委托交易
type InquiryDelegationResp struct { //Delegation
	DelegatorAddr string 		`json:"delegator_addr"`
	ValidatorAddr string 		`json:"validator_addr"`
	Shares        string        `json:"shares"`
	Height        string        `json:"height"` // Last height bond updated
}

// 委托
type DelegationsReq struct { //EditDelegationsBody
	Basetx			    BaseTx                    `json:"base_tx"`
	Delegations         []DelegationsInput        `json:"delegations"`
	BeginUnbondings     []BeginUnbondingInput     `json:"begin_unbondings"`
	CompleteUnbondings  []CompleteUnbondingInput  `json:"complete_unbondings"`
	BeginRedelegates    []BeginRedelegateInput    `json:"begin_redelegates"`
	CompleteRedelegates []CompleteRedelegateInput `json:"complete_redelegates"`
}

type DelegationAmount struct {
	Denom             string  	        `json:"denom"`
	Amount            string            `json:"amount"`
}

type DelegationsInput struct { //msgDelegationsInput
	ValidatorAddr string                `json:"validator_addr"` // in bech32
	Delegation    DelegationAmount      `json:"delegation"`
}

type BeginUnbondingInput struct { //msgBeginUnbondingInput
	ValidatorAddr string `json:"validator_addr"` // in bech32
	SharesAmount  string `json:"shares"`
}

type CompleteUnbondingInput struct {//msgCompleteUnbondingInput
	ValidatorAddr string `json:"validator_addr"` // in bech32
}

type BeginRedelegateInput struct { //BeginRedelegateInput
	ValidatorSrcAddr string `json:"validator_src_addr"` // in bech32
	ValidatorDstAddr string `json:"validator_dst_addr"` // in bech32
	SharesAmount     string `json:"shares"`
}

type CompleteRedelegateInput struct { //msgCompleteRedelegateInput
	ValidatorSrcAddr string `json:"validator_src_addr"` // in bech32
	ValidatorDstAddr string `json:"validator_dst_addr"` // in bech32
}

// 提交提议
type ProposalAmount struct {
	Denom             string  	        `json:"denom"`
	Amount            string            `json:"amount"`
}

type PostProposalReq struct { //postProposalReq
	Basetx		   BaseTx           `json:"base_tx"`
	Title          string           `json:"title"`           //  Title of the proposal
	Description    string           `json:"description"`     //  Description of the proposal
	ProposalType   ProposalKind     `json:"proposal_type"`   //  Type of proposal. Initial set {PlainTextProposal, SoftwareUpgradeProposal}
	Proposer       string           `json:"proposer"`        //  Address of the proposer
	InitialDeposit []ProposalAmount           `json:"initial_deposit"` // Coins to add to the proposal's deposit
	//Params        Params    //未实现
}

type Params []Param

type Param struct {
	Key   string `json:"key"`
	Value string `json:"value"`
	Op    Op `json:"op"`
}

type ProposalKind string

const (
	ProposalTypeText            ProposalKind = "Text"
	ProposalTypeParameterChange ProposalKind = "ParameterChange"
	ProposalTypeSoftwareUpgrade ProposalKind = "SoftwareUpgrade"
)

type Op string

const (
	Add    Op = "add"
	Update Op = "update"
)


// Text Proposals
type InquiryProposalResp struct {
	ProposalID   string       	 `json:"proposal_id"` //  ID of the proposal
	Title        string      	 `json:"title"`         //  Title of the proposal
	Description  string       	 `json:"description"`   //  Description of the proposal
	ProposalType ProposalKind	 `json:"proposal_type"` //  Type of proposal. Initial set {PlainTextProposal, SoftwareUpgradeProposal}

	Status string 				 `json:"proposal_status"` //  Status of the Proposal {Pending, Active, Passed, Rejected}

	Tally_Result TallyResult     `json:"tally_result"`

	SubmitBlock  string          `json:"submit_block"`  //  Height of the block where TxGovSubmitProposal was included
	TotalDeposit []string 		 `json:"total_deposit"` //  Current deposit on this proposal. Initial value is set at InitialDeposit

	VotingStartBlock string 	 `json:"voting_start_block"` //  Height of the block where MinDeposit was reached. -1 if MinDeposit is not reached
}

type TallyResult struct {
	Yes         string  `json:"yes"`
	Abstain     string  `json:"abstain"`
	No          string  `json:"no"`
	NoWithVeto  string  `json:"no_with_veto"`
}

// 赞助提议 抵押代币
type DepositReq struct { //depositReq
	Basetx	   BaseTx                 `json:"base_tx"`
	Depositer  string 		          `json:"depositer"` // Address of the depositer
	Amount     []ProposalAmount       `json:"amount"`    // Coins to add to the proposal's deposit
}

// 投票提议
type VoteReq struct { //voteReq
	Basetx	 BaseTx            `json:"base_tx"`
	Voter    string 			`json:"voter"`  //  address of the voter
	Option   string 			`json:"option"` //  option from OptionSet chosen by the voter
}

const (
	VoteOptionYes                 = "Yes"
	VoteOptionAbstain             = "Abstain"
	VoteOptionNo                  = "No"
	VoteOptionNoWithVeto          = "NoWithVeto"
)

// 查询投票
type VoteResp struct { //Vote
	Voter      string 		     `json:"voter"`       //  address of the voter
	ProposalID string            `json:"proposal_id"` //  proposalID of the proposal
	Option     string     		 `json:"option"`      //  option from OptionSet chosen by the voter
}

// iris version
type VersionInfo struct {
	IrisVersion    string `json:"iris_version"`
	UpgradeVersion int64  `json:"upgrade_version"`
	StartHeight    int64  `json:"start_height"`
	ProposalId     int64  `json:"proposal_id"`
}