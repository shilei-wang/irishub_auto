package main

import (
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"os"
	"strings"
)

type Pubkey struct{
	Type string `json:"type"`
	Value string `json:"value"`
}

type Validator struct{
	Address string `json:"address"`
	Pub_key Pubkey `json:"pub_key"`
	Power   string `json:"power"`
	Name    string `json:"name"`
}

type Validators struct{
	Val []Validator `json:"validators"`
}

type Account struct{
	Number   string `json:"account_number"`
	Address  string `json:"address"`
	Coins    []string `json:"coins"`
	Sequence string `json:"sequence_number"`
}

type Accounts struct{
	Acc []Account `json:"accounts"`
}

///

type Value_s struct{
	Address   string `json:"address"`
	Coins     []Coin `json:"coins"`
	Pub_key   Pubkey `json:"public_key"`
	Number    string `json:"account_number"`
	Sequence  string `json:"sequence"`
	Memo      string `json:"memo_regexp"`
}

type Bank struct{
	Type   string `json:"type"`
	Value  Value_s `json:"value"`
}

type Coin struct{
	Denom   string `json:"denom"`
	Amount  string `json:"amount"`

}








var Err         = errors.New("")
var UriAccountInfo = "http://192.168.150.38:1317/bank/accounts/%v"

func read(path string) (string,error) {
	fi, Err := os.Open(path)
	if Err != nil {
		return "",Err
	}
	defer fi.Close()

	fd, Err := ioutil.ReadAll(fi)
	return string(fd),nil
}

func readValidators(file string) Validators{
	start := strings.LastIndex(file, "validators")
	end   := strings.Index(file, "app_hash")
	validators_s := "{"+file[start-1 : end-5]+"}"
	//fmt.Println(validators_s)

	var validators Validators

	if Err = json.Unmarshal([]byte(validators_s), &validators); Err != nil {
		fmt.Println("转换validators失败")
		fmt.Println(Err)
	}

	//fmt.Println(validators.Val[0].Power)
	return validators
}

func readAccounts(file string) Accounts{
	start := strings.Index(file, "accounts")
	end   := strings.Index(file, "asset")
	//注意end-7 这里很容易出错， 多个逗号，需要注意
	accounts_s := "{"+file[start-1 : end-7]+"}"
	//fmt.Println(validators_s)

	var accounts Accounts

	if Err = json.Unmarshal([]byte(accounts_s), &accounts); Err != nil {
		fmt.Println("转换accounts_s失败")
		fmt.Println(Err)
	}

	//fmt.Println(validators.Val[0].Power)
	return accounts
}

func HttpClientGetData(uri string) (int, []byte, error) {
	res, err := http.Get(uri)
	defer res.Body.Close()

	if err != nil {
		return 0, nil, err
	}
	if res == nil {
		return 0, nil, err
	}
	defer res.Body.Close()
	resByte, err := ioutil.ReadAll(res.Body)
	if err != nil {
		return 0, nil, err
	}

	return res.StatusCode, resByte, nil
}


func checkAccounts(accounts Accounts) {

	for i, _ := range accounts.Acc {
		uri := fmt.Sprintf(UriAccountInfo, accounts.Acc[i].Address)
		statusCode, resByte, Err := HttpClientGetData(uri)

		if Err != nil {
			fmt.Println("HttpClientGetData failed", Err.Error())
			return
		}

		var bank Bank

		if statusCode == 200 {
			if Err = json.Unmarshal([]byte(resByte), &bank); Err != nil {
				fmt.Println("转换bank失败")
				fmt.Println(Err)
			}
			amount_rest := bank.Value.Coins[0].Amount
			amount_file := strings.Replace(accounts.Acc[i].Coins[0], "iris-atto","",1)
			fmt.Print("[",i,"] ",amount_rest, "," ,amount_file)

			if (amount_rest == amount_file) {
				fmt.Println(" equal!")
			} else {
				//fmt.Print("[",i,"] ",amount_rest, "," ,amount_file)
				fmt.Println(" NOT EQUAL !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! ",accounts.Acc[i].Address)
			}

		} else {
			fmt.Println("HttpClientGetData failed statusCode：", statusCode)
			return
		}


	}





	return
}



func main() {
	fmt.Println("checkGenesis.go")

	str  := ""
	file := "/Users/sherlock/genesis_export.json"

	if str, Err = read(file); Err != nil {
		fmt.Println("Open file failed [Err:%s]", Err.Error())
		return
	}

	validators := readValidators(str)
	//fmt.Println(validators.Val[0].Power)

	//accounts := readAccounts(str)
	//checkAccounts(accounts)




}