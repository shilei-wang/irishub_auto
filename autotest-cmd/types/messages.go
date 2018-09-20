/*********************
	Message 定义
 *********************/

package types

// Common 基本操作模块
const (
	MSG_TO                          =  " ------> "
	MSG_START_SEND                  =  " , START send iris, Waiting...... "
	MSG_END_SEND                    =  " , END   send iris. "
	MSG_START_DELEGATE              =  " , START delegate iris, Waiting...... "
	MSG_END_DELEGATE                =  " , END   delegate iris. "
	MSG_START_PROPOSAL              =  " , START proposal, Waiting...... "
	MSG_END_PROPOSAL                =  " , END   proposal. "
	MSG_START_DEPOSIT               =  " , START deposit proposal, Waiting...... "
	MSG_END_DEPOSIT                 =  " , END   deposit proposal. "
	MSG_START_VOTE                  =  " , START vote proposal, Waiting...... "
	MSG_END_VOTE                    =  " , END   vote proposal. "

	MSG_START_BLOCKWAIT             =  "--------- START wait for block time , Wait 5 seconds...... "
	MSG_END_BLOCKWAIT               =  "--------- END   wait for block time . "

	MSG_ACTUAL                      =  " actual data : "
	MSG_EXPECTED                    =  " expected data:  "
)

// Faucet 水龙头 模块
const (
	MSG_FAUCET_DELETEALL            =  " Faucet delete all accounts ....."
	MSG_FAUCET_INIT_OK              =  " Faucet Init Ok! Start concurrent testing now ....."
	MSG_FAUCET_INIT_START           =  " Faucet Init start, please wait ....."
)

