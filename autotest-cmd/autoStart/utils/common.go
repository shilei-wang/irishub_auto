package utils

import (
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

		//account
		str = strings.Replace(str, "150000000000000000000iris-atto", "2000000000000000000000000000iris-atto", 4)

		//auth
		str = strings.Replace(str, "\"gas_price_threshold\": \"6000000000000\"", "\"gas_price_threshold\": \"20000000000\"", 1)

		//Stake
		str = strings.Replace(str, "\"unbonding_time\": \"1814400000000000\"", "\"unbonding_time\": \"4000000000\"", 1)

		//Gov
		//critical_min_deposit, important_min_deposit
		str = strings.Replace(str, "\"critical_deposit_period\": \"86400000000000\"", "\"critical_deposit_period\": \"10000000000\"", 1)
		str = strings.Replace(str, "\"important_deposit_period\": \"86400000000000\"", "\"important_deposit_period\": \"10000000000\"", 1)
		str = strings.Replace(str, "\"normal_deposit_period\": \"86400000000000\"", "\"normal_deposit_period\": \"10000000000\"", 1)

		str = strings.Replace(str, "\"critical_voting_period\": \"120000000000\"", "\"critical_voting_period\": \"10000000000\"", 1)
		str = strings.Replace(str, "\"important_voting_period\": \"120000000000\"", "\"important_voting_period\": \"10000000000\"", 1)
		str = strings.Replace(str, "\"normal_voting_period\": \"120000000000\"", "\"normal_voting_period\": \"10000000000\"", 1)

		str = strings.Replace(str, "\"important_max_num\": \"5\"", "\"important_max_num\": \"10000\"", 1)
		str = strings.Replace(str, "\"normal_max_num\": \"2\"", "\"normal_max_num\": \"10000\"", 1)

		str = strings.Replace(str, "critical_threshold\": \"0.8340000000\"", "critical_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "important_threshold\": \"0.8000000000\"", "important_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_threshold\": \"0.6670000000\"", "normal_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "critical_veto\": \"0.3340000000\"", "critical_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "important_veto\": \"0.3340000000\"", "important_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_veto\": \"0.3340000000\"", "normal_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "critical_participation\": \"0.8572000000\"", "critical_participation\": \"0.499\"", 1)
		str = strings.Replace(str, "important_participation\": \"0.8340000000\"", "important_participation\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_participation\": \"0.7500000000\"", "normal_participation\": \"0.499\"", 1)

		str = strings.Replace(str, "\"critical_penalty\": \"0.0009000000\"", "\"critical_penalty\": \"0.0000000001\"", 1)
		str = strings.Replace(str, "\"important_penalty\": \"0.0007000000\"", "\"important_penalty\": \"0.0000000001\"", 1)
		str = strings.Replace(str, "\"normal_penalty\": \"0.0005000000\"", "\"normal_penalty\": \"0.0000000001\"", 1)

		//service
		str = strings.Replace(str, "\"complaint_retrospect\": \"1296000000000000\"", "\"complaint_retrospect\": \"1\"", 1)
		str = strings.Replace(str, "\"arbitration_time_limit\": \"432000000000000\"", "\"arbitration_time_limit\": \"1\"", 1)

		str = strings.Replace(str, "\"max_request_timeout\": \"100\"", "\"max_request_timeout\": \"5\"", 1) //????
		str = strings.Replace(str, "\"slash_fraction\": \"0.0010000000\"", "\"slash_fraction\": \"0.0000000001\"", 1)

		//mint
		str = strings.Replace(str, "\"inflation\": \"0.0400000000\"", "\"inflation\": \"0.0000000000\"", 1)



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

func AddMainnetAccount() error {
	Params := []string{"node0","node1"}
	secret := []string{
		"wear move feel play dismiss setup pudding earn sleep child move vocal bread ginger bleak kind axis raven clip odor mind gift ride method",
		"symptom picnic secret profit strike promote couch present rude page tag auto despair north biology market find veteran still welcome core depend fiction stamp"}

	for i, param := range Params {
		Params = []string{"keys", "add", param,"--recover"}

		fmt.Println("Add account "+param)
		Common.RequestWorker.MakeRequest("iriscli", Params, []string{PASSWORD,secret[i]})
	}

	return nil
}

func ModifyVerison() error {
	file := HOME+"go/src/github.com/irishub/version/patches.go"
	str  := ""

	if str,Err = read(file); Err != nil {
		return Err
	}

	str = strings.Replace(str, "H001_UNDELEGATE_PATCH = 1159800", "H001_UNDELEGATE_PATCH = 1", -1)

	fmt.Println(str)

	if Err = write(file, str); Err != nil {
		fmt.Println(Err.Error())
		return Err
	}

	return nil
}

func AddAccount(num string) error{
	n, _ := strconv.Atoi(num)

	Params := []string{"v0","v1","v2","v3"}

	for i, param := range Params {
		if i == n {break}

		Params = []string{"keys", "add", param,"--recover"}

		fmt.Println("Add account "+param)

		str  := ""
		file := HOME+"testnet/"+param+"/iriscli/key_seed.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		secret := find_substr(str,3,4)

		//注意：1.repeat类型的password只读一次，只需要输入一2.以"\n"为分隔符读取不同行的数据  3.无需等待一次性输入
		//例子：stdin.Write([]byte("y"+ "\n"+Inputs[1]+ "\n"))
		Common.RequestWorker.MakeRequest("iriscli", Params, []string{PASSWORD,secret})
	}

	Params = []string{"node0","node1","node2","node3"}

	for i, param := range Params {
		if i == n {break}

		Params = []string{"keys", "add", param,"--recover"}

		fmt.Println("Add account "+param)

		p := param
		if (p == "node0"){
			p = "v0"
		}else if (p == "node1"){
			p = "v1"
		}else if (p == "node2"){
			p = "v2"
		}else if (p == "node3"){
			p = "v3"
		}

		str  := ""
		file := HOME+"testnet/"+p+"/iriscli/key_seed.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		secret := find_substr(str,3,4)

		//注意：1.repeat类型的password只读一次，只需要输入一2.以"\n"为分隔符读取不同行的数据  3.无需等待一次性输入
		//例子：stdin.Write([]byte("y"+ "\n"+Inputs[1]+ "\n"))
		Common.RequestWorker.MakeRequest("iriscli", Params, []string{PASSWORD,secret})
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

func ModifyGenesis_c(num string) error{
	//return nil
	Params := []string{"v0","v1","v2","v3"}
	n, _ := strconv.Atoi(num)

	for i, param := range Params {

		if i == n {break}

		str  := ""
		file := HOME+"testnet/"+param+"/iris/config/genesis.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		//account
		str = strings.Replace(str, "150000000000000000000iris-atto", "2000000000000000000000000000iris-atto", 4)

		//auth
		str = strings.Replace(str, "\"gas_price_threshold\": \"6000000000000\"", "\"gas_price_threshold\": \"20000000000\"", 1)

		//Stake
		str = strings.Replace(str, "\"unbonding_time\": \"1814400000000000\"", "\"unbonding_time\": \"10000000000\"", 1)

		//Gov
		//critical_min_deposit, important_min_deposit
		str = strings.Replace(str, "\"system_halt_period\": \"20000\"", "\"system_halt_period\": \"10\"", 1)

		str = strings.Replace(str, "\"critical_deposit_period\": \"86400000000000\"", "\"critical_deposit_period\": \"20000000000\"", 1)
		str = strings.Replace(str, "\"important_deposit_period\": \"86400000000000\"", "\"important_deposit_period\": \"20000000000\"", 1)
		str = strings.Replace(str, "\"normal_deposit_period\": \"86400000000000\"", "\"normal_deposit_period\": \"20000000000\"", 1)

		str = strings.Replace(str, "\"critical_voting_period\": \"120000000000\"", "\"critical_voting_period\": \"20000000000\"", 1)
		str = strings.Replace(str, "\"important_voting_period\": \"120000000000\"", "\"important_voting_period\": \"20000000000\"", 1)
		str = strings.Replace(str, "\"normal_voting_period\": \"120000000000\"", "\"normal_voting_period\": \"20000000000\"", 1)

		//str = strings.Replace(str, "\"normal_max_num\": \"1\"", "\"normal_max_num\": \"100000\"", 1)
		//str = strings.Replace(str, "\"critical_max_num\": \"1\"", "\"critical_max_num\": \"2\"", 1)

		str = strings.Replace(str, "critical_threshold\": \"0.8340000000\"", "critical_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "important_threshold\": \"0.8000000000\"", "important_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_threshold\": \"0.6670000000\"", "normal_threshold\": \"0.499\"", 1)
		str = strings.Replace(str, "critical_veto\": \"0.3340000000\"", "critical_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "important_veto\": \"0.3340000000\"", "important_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_veto\": \"0.3340000000\"", "normal_veto\": \"0.499\"", 1)
		str = strings.Replace(str, "critical_participation\": \"0.8572000000\"", "critical_participation\": \"0.499\"", 1)
		str = strings.Replace(str, "important_participation\": \"0.8340000000\"", "important_participation\": \"0.499\"", 1)
		str = strings.Replace(str, "normal_participation\": \"0.7500000000\"", "normal_participation\": \"0.499\"", 1)

		//str = strings.Replace(str, "\"critical_penalty\": \"0.0009000000\"", "\"critical_penalty\": \"0.0000000001\"", 1)
		//str = strings.Replace(str, "\"important_penalty\": \"0.0007000000\"", "\"important_penalty\": \"0.0000000001\"", 1)
		//str = strings.Replace(str, "\"normal_penalty\": \"0.0005000000\"", "\"normal_penalty\": \"0.0000000001\"", 1)

		//block windows
		//str = strings.Replace(str, "signed_blocks_window\": \"34560\"", "signed_blocks_window\": \"10\"", 1)


		//commission rate
		str = strings.Replace(str, "\"rate\": \"0.0000000000\"", "\"rate\": \"0.1000000000\"", 1)
		str = strings.Replace(str, "\"max_rate\": \"0.0000000000\"", "\"max_rate\": \"0.2000000000\"", 1)
		str = strings.Replace(str, "\"max_change_rate\": \"0.0000000000\"", "\"max_change_rate\": \"0.0100000000\"", 1)

		//service
		//str = strings.Replace(str, "\"complaint_retrospect\": \"1296000000000000\"", "\"complaint_retrospect\": \"20000000000\"", 1)
		//str = strings.Replace(str, "\"arbitration_timelimit\": \"432000000000000\"", "\"arbitration_timelimit\": \"20000000000\"", 1)

		str = strings.Replace(str, "\"max_request_timeout\": \"100\"", "\"max_request_timeout\": \"20\"", 1)
		//str = strings.Replace(str, "\"slash_fraction\": \"0.0100000000\"", "\"slash_fraction\": \"1.0000000000\"", 1)

		//mint
		str = strings.Replace(str, "\"inflation\": \"0.0400000000\"", "\"inflation\": \"0\"", 1)
		str = strings.Replace(str, "\"downtime-unbond-duration\": \"172800000000000\"", "\"downtime-unbond-duration\": \"1\"", 1)
		str = strings.Replace(str, "\"censorship-jail-duration\": \"604800000000000\"", "\"censorship-jail-duration\": \"1\"", 1)


		//str = strings.Replace(str, "\"max_validators\": 100", "\"max_validators\": 2", 1)


		if Err := write(file, str); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}

func ModifyGenesis_nm(num string) error{
	//return nil
	Params := []string{"v0","v1","v2","v3"}
	n, _ := strconv.Atoi(num)

	for i, param := range Params {

		if i == n {break}

		str  := ""
		file := HOME+"testnet/"+param+"/iris/config/genesis.json"

		if str,Err = read(file); Err != nil {
			return Err
		}

		//account
		str = strings.Replace(str, "150000000000000000000iris-atto", "2000000000000000000000000000iris-atto", 4)
		str = strings.Replace(str, "\"gas_price_threshold\": \"6000000000000\"", "\"gas_price_threshold\": \"20000000000\"", 1)

		str = strings.Replace(str, "\"normal_max_num\": \"2\"", "\"normal_max_num\": \"100000\"", 1)

		str = strings.Replace(str, "\"unbonding_time\": \"1814400000000000\"", "\"unbonding_time\": \"180000000000\"", 1)
		str = strings.Replace(str, "\"normal_voting_period\": \"120000000000\"", "\"normal_voting_period\": \"180000000000\"", 1)

		if Err := write(file, str); Err != nil {
			fmt.Println(Err.Error())
			return Err
		}
	}

	return nil
}