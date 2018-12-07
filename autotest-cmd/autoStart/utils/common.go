package utils

import (
	. "github.com/irishub_auto/autotest-cmd/common"
	"time"
	"fmt"
	"strings"
	"strconv"
)

func ForTest(){
	Params = []string{"keys", "list"}

	repBody,  err := Common.RequestWorker.MakeRequest("iriscli", Params, nil)
	if err != nil {
		fmt.Print("  Test  c.RequestWorker.MakeRequest ")
		return
	}

	fmt.Println(repBody)
}

func Init_testnet(num string){
	Command = "iris"
	Params = []string{"testnet", "--v="+num,"--output-dir="+HOME+"testnet","--chain-id=shilei-qa","--node-dir-prefix=v","--starting-ip-address=127.0.0.1"}
	if num == "1" {
		Common.RequestWorker.MakeRequest(Command, Params, []string{PASSWORD})
	} else {
		Common.RequestWorker.MakeRequest(Command, Params, []string{PASSWORD,PASSWORD,PASSWORD,PASSWORD})
	}
}

func ModifyGenesis(num string) error{
	Params := []string{"v0","v1","v2","v3"}
	n, _ := strconv.Atoi(num)

	for i, param := range Params {
		if i == n {break}

		str  := ""
		file := HOME+"testnet/"+param+"/iris/config/genesis.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		str = strings.Replace(str, "150000000000000000000iris-atto", "2000000000000000000000000iris-atto", 4)

		if (n == 1) {
			str = strings.Replace(str, "\"loose_tokens\": \"150000000000000000000.", "\"loose_tokens\": \"2000000000000000000000000.", 1)
		} else if  (n == 2) {
			str = strings.Replace(str, "\"loose_tokens\": \"300000000000000000000.", "\"loose_tokens\": \"4000000000000000000000000.", 1)
		} else if  (n == 4) {
			str = strings.Replace(str, "\"loose_tokens\": \"600000000000000000000.", "\"loose_tokens\": \"8000000000000000000000000.", 1)

		}

		str = strings.Replace(str, "\"voting_period\": \"172800000000000\"", "\"voting_period\": \"45000000000\"", 1)
		str = strings.Replace(str, "\"switch_period\": \"57600\"", "\"switch_period\": \"30\"", 1)
		str = strings.Replace(str, "\"signed-blocks-window\": \"100\"", "\"signed-blocks-window\": \"6\"", 1)


		str = strings.Replace(str, "\"community_tax\": \"0.0200000000\"", "\"community_tax\": \"0.1000000000\"", 1)
		str = strings.Replace(str, "\"base_proposer_reward\": \"0.0100000000\"", "\"base_proposer_reward\": \"0.0000000001\"", 1)
		str = strings.Replace(str, "\"bonus_proposer_reward\": \"0.0400000000\"", "\"bonus_proposer_reward\": \"0.0000000001\"", 1)

		str = strings.Replace(str, "\"rate\": \"0.0000000000\"", "\"rate\": \"0.1000000000\"", 1)
		str = strings.Replace(str, "\"max_rate\": \"0.0000000000\"", "\"max_rate\": \"0.2000000000\"", 1)
		str = strings.Replace(str, "\"max_change_rate\": \"0.0000000000\"", "\"max_change_rate\": \"0.0100000000\"", 1)

		str = strings.Replace(str, "\"threshold\": \"0.5000000000\"", "\"threshold\": \"0.4999999999\"", 1)
		str = strings.Replace(str, "\"veto\": \"0.3340000000\"", "\"veto\": \"0.4999999999\"", 1)
		str = strings.Replace(str, "\"participation\": \"0.6670000000\"", "\"participation\": \"0.4999999999\"", 1)

		str = strings.Replace(str, "\"terminator_period\": \"20000\"", "\"terminator_period\": \"10\"", 1)

		str = strings.Replace(str, "\"complaint_retrospect\": \"1296000000000000\"", "\"complaint_retrospect\": \"1\"", 1)
		str = strings.Replace(str, "\"arbitration_timelimit\": \"432000000000000\"", "\"arbitration_timelimit\": \"1\"", 1)

		str = strings.Replace(str, "\"MaxRequestTimeout\": \"100\"", "\"MaxRequestTimeout\": \"5\"", 1)

		str = strings.Replace(str, "\"unbonding_time\": \"600000000000\"", "\"unbonding_time\": \"1\"", 1)


		if Err := write(file, str); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func StartAndPrint(num string){
	n, _ := strconv.Atoi(num)
	Params := []string{"v0","v1","v2","v3"}

	for i, param := range Params {
		if i == n {break}

		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"start", "--home="+HOME+"testnet/"+param+"/iris"}

		if (param == "v0"){
			go Common.RequestWorker.ExecStart("iris", Params, true )
		}else{
			go Common.RequestWorker.ExecStart("iris", Params, false )
		}
	}

	time.Sleep(time.Duration(DURATION)*time.Second)
}

func ModifyToml(num string) error{
	n, _ := strconv.Atoi(num)

	str  := ""
	file := ""
	Params := []string{"v0","v1","v2","v3"}
	param_ports := []string{"5","6","7","8"}

	for i, param := range Params {
		if i == n {break}

		file = HOME+"testnet/"+param+"/iris/config/config.toml"
		if str,Err = read(file); Err != nil {
			return Err
		}

		str = strings.Replace(str, "timeout_commit = \"5s\"", "timeout_commit = \""+BLOCK_TIME+"s\"", -1)

		if (param != "v0") {
			str = strings.Replace(str, "26656", "266"+param_ports[i]+"6", 1) //only once
			str = strings.Replace(str, "26657", "266"+param_ports[i]+"7", -1)
			str = strings.Replace(str, "26658", "266"+param_ports[i]+"8", -1)
		}

		if Err := write(file, str); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func AddAccount(num string) error{
	n, _ := strconv.Atoi(num)

	Params := []string{"v0","v1","v2","v3"}

	for i, param := range Params {
		if i == n {break}

		Params = []string{"keys", "add", param,"--recover"}

		str  := ""
		file := HOME+"testnet/"+param+"/iriscli/key_seed.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		secret := find_substr(str,3,4)

		//注意：1.repeat类型的password只读一次，只需要输入一2.以"\n"为分隔符读取不同行的数据  3.无需等待一次性输入
		//例子：stdin.Write([]byte("y"+ "\n"+Inputs[1]+ "\n"))
		Common.RequestWorker.MakeRequest("iriscli", Params, []string{PASSWORD,secret})

		fmt.Println("Add account "+param)
	}

	return nil
}

func AddAccountForRest() error{
	Params := []string{"v0","v1","v2","v3"}

	for _, param := range Params {
		Params = []string{"keys", "add", param,"--recover","--home=/root/.irislcd"}

		str  := ""
		file := HOME+"testnet/"+param+"/iriscli/key_seed.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		secret := find_substr(str,3,4)

		//注意：1.repeat类型的password只读一次，只需要输入一2.以"\n"为分隔符读取不同行的数据  3.无需等待一次性输入
		//例子：stdin.Write([]byte("y"+ "\n"+Inputs[1]+ "\n"))
		Common.RequestWorker.MakeRequest("iriscli", Params, []string{PASSWORD,secret})

		fmt.Println("Add account "+param)
	}

	return nil
}


func Reset(c *CommonWorker){
	Command = "iris"
	users := []string{"iris1","iris2"}

	for _, user := range users {
		time.Sleep(time.Duration(DURATION)*time.Second)

		Params = []string{"unsafe_reset_all", "--home="+HOME+user}
		//c(Command, Params,true,false)
	}
}