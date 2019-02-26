## Iris 功能测试 结构设计 ##
![](images/1_%E6%B5%8B%E8%AF%95%E7%94%A8%E4%BE%8B%E6%A1%86%E6%9E%B6.jpg)
## Iris 测试报告 ##
![](images/2_%E8%AF%A6%E7%BB%86%E6%B5%8B%E8%AF%95%E6%8A%A5%E5%91%8A.jpg)
## Iris 功能测试 自动化框架设计 ##
![](images/3_%E7%A8%8B%E5%BA%8F%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1.jpg)
## Iris 流程设计 ##
![](images/4_%E6%B5%8B%E8%AF%95%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg)

go run /root/go/src/github.com/irishub_auto/autotest-cmd/autoStart/lanchIris.go c 1
go run /root/go/src/github.com/irishub_auto/autotest-cmd/autoStart/lanchIris.go c 2
go run /root/go/src/github.com/irishub_auto/autotest-cmd/autoStart/lanchIris.go t 2
go run /root/go/src/github.com/irishub_auto/autotest-cmd/autoStart/lanchIris.go 2
gedit /root/testnet/v0/iris/config/genesis.json
gedit /root/testnet/v0/iris/config/config.toml
iris start --home=/root/testnet/v0/iris
iris start --home=/root/testnet/v1/iris


iriscli keys show v0
v0 = faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx

iriscli keys show v0 --bech val
v0(fva) =fva125ecgnn2lgznpg208t7cgs9trddcucmg5q3n8x

[用在create validator和distribution result]
 iriscli keys add x1 --recover
caught below output final blast sun elevator labor regular palm dizzy stand arctic judge cost typical confirm people dust panic still aspect sword save
x1 = faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju

--
echo 1234567890 | iriscli bank send --amount=20000iris --fee=0.004iris --commit --chain-id=shilei-qa --from=v0 --to=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju

echo 1234567890 | iriscli bank send --amount=1iris --fee=0.004iris  --chain-id=shilei-qa --from=v0 --to=faa1hpv42g98w4pjc88n5tdxfaeda632gl3ravm0en --commit
---
iris start --home=/root/testnet/v0/iris
iris start --home=/root/testnet/v1/iris

irislcd start --node=tcp://localhost:26657 --laddr=tcp://0.0.0.0:1317 --chain-id=shilei-qa --home=$HOME/.iriscli/ --trust-node

*******************************************
               1.keys
*******************************************
1.
iriscli keys list

2.
echo 1234567890 | iriscli keys add v1

3.
iriscli keys add v1 --recover

4.
iriscli keys show v1

5.
iriscli keys update v1

6.
iriscli keys delete v1

7.//生成助记词
iriscli keys mnemonic



*******************************************
               2.Bank
                // 首先会计算扣掉fee的钱后是否有足够余额 没有checkTX的时候直接返回错误，此过程不扣fee
                // 转账后的余额 = 原余额 - 转账数额 - gasused*gasprice （gasprice= fee/gas_wanted）
*******************************************
1.  转账
iriscli bank send --amount=10000iris --fee=0.004iris  --chain-id=shilei-qa --from=v0 --to=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
iriscli tendermint tx --trust-node EE189BBF3A74893BFA17E440B21D308A55D64B2FAAAA88813E4F1DCA9FA1E898

2.  查账 0.1
iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju

3.  查cointype
iriscli bank coin-type iris


4. 构造、签名、 （这块因为要写文件，不能autotest）
4.1  构造
iriscli bank send --amount=5iris --fee=0.004iris  --chain-id=shilei-qa --from=v0 --to=faa14yxfvj8x9rdyth2884actys8ct426yk5rhr547 --generate-only >/root/Tx-generate

4.2  签名
iriscli bank sign /root/Tx-generate --name=v0 --chain-id=shilei-qa >/root/Tx-sign

4.3  广播
[查账]iriscli bank account faa14yxfvj8x9rdyth2884actys8ct426yk5rhr547
iriscli bank broadcast /root/Tx-sign --commit
[查账]iriscli bank account faa14yxfvj8x9rdyth2884actys8ct426yk5rhr547


=== 多次签名 (允许同一个人多次签名) ===
echo 1234567890 | iriscli bank sign /root/Tx-sign --name=v1 --chain-id=shilei-qa >/root/Tx-sign2
echo 1234567890 | iriscli bank sign /root/Tx-sign2 --name=v1 --chain-id=shilei-qa >/root/Tx-sign3

注：最多签7次， 超过7次broadcast时会被拒绝。（send交易， 添加多个input， 然后output总数增加的方式可以实现多签名）

5. 销毁
echo 1234567890 | iriscli bank burn --from=v0 --fee=0.004iris --commit --chain-id=shilei-qa --amount=9iris-atto


*******************************************
               3.Stake
                 //validator             Query a validator
                 //validators            Query for all validators
                 //delegation            Query a delegation based on address and validator address
                 //delegations           Query all delegations made from one delegator
                 //unbonding-delegation  Query an unbonding-delegation record based on delegator and validator address
                 //unbonding-delegations Query all unbonding-delegations records for one delegator
                 //redelegation          Query a redelegation record based on delegator and a source and destination validator address
                 //redelegations         Query all redelegations records for one delegator
                 //signing-info          Query a validator's signing information
                 //create-validator      create new validator initialized with a self-delegation to it
                 //edit-validator        edit and existing validator account
                 //delegate              delegate liquid tokens to an validator
                 //unbond                unbond shares from a validator
                 //redelegate            redelegate illiquid tokens from one validator to another
                 //unjail                unjail validator previously jailed for downtime
*******************************************
1. 查看所有validators， 获取fva（operator address） fvp （pubkey fvp）
iriscli stake validators

2. 查看单个validator， 获取fva（operator address） fvp （pubkey fvp）
iriscli stake validator fva1d3s3ypjewrjs82gx2a02ur9mhpvjk88qr6e6s8

3. [交易] 抵押委托
echo 1234567890 | iriscli stake delegate --address-validator=fva1u8ejg354zxprwwhulzyp9v07j33clju4yka5mm --amount=5iris --fee=0.004iris --commit  --from=v0  --chain-id=shilei-qa

===
echo 1234567890 | iriscli stake delegate --address-validator=fva12zwtvdu0w57w69uqlh9js4kv0fyu5lmsvfx4l4 --amount=10iris --fee=0.004iris --commit --from=x1  --chain-id=shilei-qa

4.
//查询所有delegator的所有delegations
iriscli stake delegations faa1wnypffmv3k5rw6erfpmg4cyn8nr5ne08mgljmw
//查询所有validator的所有delegations
iriscli stake delegations-to fva12zwtvdu0w57w69uqlh9js4kv0fyu5lmsvfx4l4

5. 查询单个delegation
iriscli stake delegation --address-delegator=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju --address-validator=fva1ehy4u03qaz885f6v76v5x3r054z69zl694hvmn

6.[交易] 解绑委托
echo 1234567890 | iriscli stake unbond --address-validator=fva12zwtvdu0w57w69uqlh9js4kv0fyu5lmsvfx4l4 --shares-amount=1 --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa

echo 1234567890 | iriscli stake unbond --address-validator=fva19d4ts6dl275wf6cne06cs2cg484kjeh3mz9jq4 --shares-percent=0.5 --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa


7. 查询单个解绑 (10分钟后到账)
iriscli stake unbonding-delegation --address-delegator=faa1ur7t9r89pvhmdyawrc9lr0y77l6lr5zclnajru --address-validator=fva1ur7t9r89pvhmdyawrc9lr0y77l6lr5zc2zha7m
【通过命令可以看当前时间】 date -u "+%Y-%m-%dT%H:%M:%SZ"

8. 查询所有解绑
//查delegator faa
iriscli stake unbonding-delegations faa1suh73zlakzm50qj88d9r6d5ya8wvvmatwv9cck
//查validator fva
iriscli stake unbonding-delegations-from fva1r3qdkfw4tpxthhwv770y9vmg5hlnhm92ftyfmj


9. 查看jail的情况， 和miss的区块数量 。 [pubkey fvp 通过 iriscli stake validators]
iriscli stake signing-info fvp1zcjduepqn8l4dj70m2jpk0r9gny4ml5k2zsmq3araezfvj4azh37lkl4q9dswjez0v

10.
10.1 获取      ： node id   //d6ab89f9e3cca940cb17ab93fcdc37e092f9988f
        iris tendermint show-node-id --home=/root/testnet/v0/iris

10.2 获取节点的： fva  ，  pubkey (普通节点的pubkey 验证节点的是fvp)
        获取      ： fva
        // 实际validator的fva = iriscli stake validators中的fva
        iriscli keys show v0 --bech val

10.3 获取节点的：fcp （create validator用到）
    iris tendermint show-validator

10.4 获取节点的：fvp （slash -> 获取signing-info）
    iriscli stake validators

10.5 获取节点：fca  没有什么用
    iris tendermint show-address --home=/root/testnet/v0/iris


11. [交易] 创建验证人
iriscli stake create validator

====== 0.7.0（0.26）======
iris init --home=/root/x1 --chain-id=shilei-qa  --moniker=x1
copy genesis toml
iris tendermint show-node-id --home=/root/testnet/v0/iris  //node id 下面用
toml 26657=》26687 3个，seeds = "d6ab89f9e3cca940cb17ab93fcdc37e092f9988f@localhost:26656"
iris start --home=/root/x1 （此时正常追赶）

iriscli keys add x1 --recover (上面已经创建？faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju)
iriscli keys show x1 //faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
iriscli bank send --amount=1000iris --fee=0.004iris  --chain-id=shilei-qa --from=v0 --to=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju


iris tendermint show-validator --home=/root/x1  //fcp1zcjduepqusx50xvlpkt2sw46qd92x9k9f3p69t4vcnr5xzz50dwn6leql2pq4qywer
echo 1234567890 | iriscli stake create-validator --pubkey=fcp1zcjduepqusx50xvlpkt2sw46qd92x9k9f3p69t4vcnr5xzz50dwn6leql2pq4qywer --moniker=x1 --amount=5iris --fee=0.004iris --from=x1 --chain-id=shilei-qa --commission-rate=0.21  //这2个参数被取消了 --commission-max-rate=0.51  --commission-max-change-rate=0.11  默认都为1 且不能改
[注意抵押代币不要>1/3,  对后续unjail测试有影响]
iriscli stake validators

单个查询
iriscli stake validator fva1285anct6gnl7zkdnvm34788pu9wrcyhk4nt6el  --chain-id=shilei-qa
==========================

12. [交易] 编辑验证人 （只能更改--commission-rate）
echo 1234567890 | iriscli stake edit-validator --details=details --moniker=moniker --website=website  --fee=0.004iris --from=v0 --chain-id=shilei-qa  --commission-rate="0.11" --commit
【验证】iriscli stake validators

13. [交易] redelegate  （从iriscli stake validators读出fva）
echo 1234567890 | iriscli stake redelegate --address-validator-source=fva1d3s3ypjewrjs82gx2a02ur9mhpvjk88qr6e6s8 --address-validator-dest=fva1lcuw6ewd2gfxap37sejewmta205sgssme9ru0m --shares-amount=3 --fee=0.004iris --from=v0 --chain-id=shilei-qa --commit
【验证】iriscli stake validators

14. 查询（某人）单个 redelegation
iriscli stake redelegation --address-validator-source=fva1d3s3ypjewrjs82gx2a02ur9mhpvjk88qr6e6s8 --address-validator-dest=fva1lcuw6ewd2gfxap37sejewmta205sgssme9ru0m --address-delegator=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx

15. 查询（某人）所有 redelegations
//查询所有delegator的所有redelegations
iriscli stake redelegations faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx
//查询所有validator的所有redelegations
iriscli stake redelegations-from fva1r3qdkfw4tpxthhwv770y9vmg5hlnhm92ftyfmj

16. 查询 stake pool
iriscli stake pool

16. 查询 stake parameters
iriscli stake parameters

*******************************************
               4.Slash
*******************************************

16. [交易] unjail // genesis中改 100块代码 ，目前改成6个块 的1/2// genesis中signed-blocks-window， 代码中的DowntimeUnbondDuration
kill x1
iriscli stake validators //等3个块 显示jail=true ， 记录fvp
iriscli stake signing-info fcp1zcjduepq0vc5hsgga4lmwr7w3ld2eftgd93wx6z2yuc47j3gf6z38e04v9hqra955q
【查询时间】date -u "+utc %Y-%m-%d  %H:%M:%S"
iris start --home=/root/x1
echo 1234567890 | iriscli stake unjail --fee=0.004iris --commit --from=x1 --chain-id=shilei-qa
iriscli stake validators


iriscli stake unjail --fee=0.004iris --commit --from=v1 --chain-id=shilei-qa

*******************************************
               5.Gov
                   提交提议 iriscli gov submit-proposal
                   赞助提议 iriscli gov deposit
                   对提议投票 iriscli gov vote
                   查询提议 iriscli gov query-proposal 、iriscli gov query-proposals
                   查询赞助 iriscli gov query-deposit 、iriscli gov query-deposits
                   查询投票 iriscli gov query-vote 、iriscli gov query-votes
                   查询tally iriscli gov query-tally
*******************************************
1. [交易] 提交提议

echo 1234567890 | iriscli gov submit-proposal --chain-id=shilei-qa --from=v0 --fee=0.05iris --commit --description=t --title=t --usage="Burn" --percent=0.0000000001 --type="TxTaxUsage"  --deposit=990iris


2. 查询提议
iriscli gov query-proposal --trust-node --proposal-id=1

===
iriscli gov query-proposal --trust-node --proposal-id=4


3.[交易] 赞助提议
echo 1234567890 | iriscli gov deposit  --deposit=1iris-atto --fee=0.004iris --commit --from=v1 --trust-node --chain-id=shilei-qa --proposal-id=1
echo 1234567890 | iriscli gov deposit  --deposit=100iris --fee=0.004iris --commit --from=v0 --trust-node --chain-id=shilei-qa --proposal-id=1


【验证】iriscli gov query-proposal --trust-node --proposal-id=1

===
iriscli gov deposit  --deposit=100iris --fee=0.004iris --from=v0 --trust-node --chain-id=shilei-qa --proposal-id=12


4.[交易] 投票提议
echo 1234567890 | iriscli gov vote --option=Yes --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa  --proposal-id=1

echo 1234567890 | iriscli gov vote --option=Yes --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa  --proposal-id=1

===


5. 查询投票和赞助。 proposal生命周期结束会删除 （最后修改 vote peroid时候在看 时间太短）
iriscli gov query-vote      --voter=faa12xwh42s7k0a6s6hhszatektahewegg2flfjpu4 --chain-id=shilei-qa --proposal-id=1
iriscli gov query-votes     --proposal-id=1
iriscli gov query-deposit   --depositor=faa12xwh42s7k0a6s6hhszatektahewegg2flfjpu4 --proposal-id=12
iriscli gov query-deposits  --proposal-id=12

6. 查询提议
iriscli gov query-proposals
iriscli gov query-proposals --limit=1
iriscli gov query-proposal --proposal-id=1 --trust-node

7. 查询投票统计
iriscli gov query-tally --proposal-id=1

8. param

8.1 查询可治理参数
    iriscli gov query-params --trust-node --module=stake

8.2 [交易] 发起修改参数提议
    echo 1234567890 | iriscli gov submit-proposal --title="t" --description="t" --type="ParameterChange" --deposit="2000iris"  --param='stake/UnbondingTime=0h1m0s' --from=v0 --chain-id=shilei-qa --fee=0.004iris  --commit


9. SoftwareHalt
//这个提议只能由profiler提出（其他账户报错）， 一旦通过，后续Terminator提议全部失效， 这里要结束重启
//iriscli guardian profilers

echo 1234567890 | iriscli gov submit-proposal --trust-node --title=test --description=test --type=SystemHalt  --deposit=4000iris --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa

    共识会在一定块数后shutdown （"terminator_period": "10"）
    iriscli gov vote --option=Yes --fee=0.004iris --from=v0 --chain-id=shilei-qa --proposal-id=16
    iriscli gov query-proposal --trust-node --proposal-id=3

10. TxTaxUsage （这里处理的是交易产生的fee所收的tax）
   // 社区基金使用提议 Burn=销毁  Distribute,Grant=转账（只能转给trustees 账户）
   //先产生fee
   iriscli bank send --amount=1iris-atto --to=faa125ecgnn2lgznpg208t7cgs9trddcucmgp3mu6p --from=v0 --gas=10000 --fee=2260.9575iris --chain-id=shilei-qa

   ====
   echo 1234567890 | iriscli gov submit-proposal --chain-id=shilei-qa --from=v0 --fee=0.05iris --commit --deposit="1000iris" --description="test"  --title="burn tokens 5%" --type="TxTaxUsage" --usage="Burn" --percent=0.05

   echo 1234567890 | iriscli gov submit-proposal --chain-id=shilei-qa --from=v0 --fee=0.05iris --commit --deposit="1000iris" --description="test" --title="Distribute tokens 99%" --type="TxTaxUsage" --usage="Distribute" --percent=0.9999 --dest-address=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju

   echo 1234567890 | iriscli gov submit-proposal --chain-id=shilei-qa --from=v0 --fee=0.05iris --commit --deposit="1000iris" --description="test" --title="Grant tokens 99%" --type="TxTaxUsage" --usage="Grant" --percent=0.99 --dest-address=faa125ecgnn2lgznpg208t7cgs9trddcucmgp3mu6p
   ====

   ==== 检查 ====
   iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
   echo 1234567890 | iriscli gov vote --fee=0.004iris --commit --from=v0 --chain-id=shilei-qa  --proposal-id=3  --option=Yes
   iriscli gov query-proposal --trust-node --proposal-id=3
   iriscli bank account faa1kepfks8l9s5h2yrglgq2vmarrdt0v9s7e8e5e5



*******************************************
               6.Upgrade
*******************************************
单独测

*******************************************
【暂时删除】    7.record
*******************************************
1. [交易] 提交存证
iriscli record submit --chain-id=shilei-qa --description="hahaha" --onchain-data="shelwin flighting" --from=v0 --fee=0.004iris

2. 查询存证
iriscli record query --chain-id=shilei-qa --record-id="record:5aa096c3cdf0c0c7eaa39c430b3e405edb1903313949cd5148422f6bf3ce707a"

3. 下载存证 （注意 --path）
iriscli record download --chain-id=shilei-qa --file-name=record --record-id="record:0e60b8507c1e58678ab1fa01312cf5d790cfcccba094b24b376dc91e2cb7a445"
【验证】gedit /root/.iriscli/record

iriscli record download --chain-id=shilei-qa --file-name=record --record-id="record:0e60b8507c1e58678ab1fa01312cf5d790cfcccba094b24b376dc91e2cb7a445" --path=/root

*******************************************
               8.service
*******************************************
     ===========================
                定义
     ===========================
1. [交易] 服务定义
echo 1234567890 | iriscli service define --chain-id=shilei-qa  --from=v0 --fee=0.004iris --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content=idl-content --file=/root/test.proto --service-name=001 --commit

【2个method】
iriscli service define --chain-id=shilei-qa  --from=v0 --fee=0.004iris --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content=idl-content --file=/root/test2.proto --service-name=002



iriscli service define --chain-id=shilei-qa  --from=v0 --fee=0.004iris --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content="syntax = \"proto3\";\n\npackage helloworld;\n\n// The greeting service definition.\nservice Greeter {\n    //@Attribute description: sayHello\n    //@Attribute output_privacy: NoPrivacy\n    //@Attribute output_cached: NoCached\n    rpc SayHello (HelloRequest) returns (HelloReply) {}\n\n\n}\n\n// The request message containing the user's name.\nmessage HelloRequest {\n    string a = 1;\n    string b = 2;\n}\n\n// The response message containing the greetings\nmessage HelloReply {\n    string c = 3;\n    string d = 4;\n}\n"  --service-name=002


===
iriscli service define --chain-id=shilei-qa  --from=v0 --fee=0.004iris --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content=idl-content --file=/root/test.proto --service-name=001


2. 服务定义查询 （可以查询 method id）
iriscli service definition --def-chain-id=shilei-qa --service-name=001

     ===========================
                绑定
     ===========================
3. [交易] 服务绑定
echo 1234567890 | iriscli service bind --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --def-chain-id=shilei-qa --bind-type=Local --deposit=2000iris --prices=1iris --avg-rsp-time=10000 --usable-time=100 --service-name=001

【2个method】
iriscli service bind --chain-id=shilei-qa --from=v0 --fee=0.004iris --def-chain-id=shilei-qa --bind-type=Local --deposit=2000iris --prices=1iris,0iris --avg-rsp-time=10000 --usable-time=100 --service-name=002

===
iriscli service bind --chain-id=shilei-qa --from=v0 --fee=0.004iris --def-chain-id=shilei-qa --bind-type=Local --deposit=200iris --prices=2iris --avg-rsp-time=10000 --usable-time=100 --service-name=001

4. 查询（某个）服务提供者的单个服务绑定 [--provider 要改]
iriscli service binding --def-chain-id=shilei-qa --bind-chain-id=shilei-qa --provider=faa19efxje3sgfcm842azsfa3xjckfrrqv2y7u24t7 --service-name=001

===
iriscli service binding --def-chain-id=shilei-qa --bind-chain-id=shilei-qa --provider=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --service-name=005

5. 查询（某个）服务 所有的绑定人的情况
iriscli service bindings --def-chain-id=shilei-qa --service-name=001

===
iriscli service bindings --def-chain-id=shilei-qa --service-name=a004


6. [交易] 更新绑定
echo 1234567890 | iriscli service update-binding --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --bind-type=Local  --deposit=100iris --prices=1iris --avg-rsp-time=9999 --usable-time=99 --service-name=002

【验证】 iriscli service bindings --def-chain-id=shilei-qa --service-name=001

===
iriscli service update-binding --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --bind-type=Local  --deposit=2000iris --prices=8iris-atto --avg-rsp-time=-1 --usable-time=1 --service-name=a001


7.服务失效
echo 1234567890 | iriscli service disable --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --service-name=001
---
iriscli service disable --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --service-name=005

8.服务恢复
echo 1234567890 | iriscli service enable  --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --deposit=1000iris --service-name=001
---
iriscli service enable  --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --deposit=999iris --service-name=001

9. [交易] 取回抵押 (滞后 COMPLAINT_RETROSPECT (Gov) + ARBITRATION_TIMELIMIT (Gov))
echo 1234567890 | iriscli service refund-deposit --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --service-name=001

【通过命令可以看当前时间】 date -u "+%Y-%m-%dT%H:%M:%SZ"
===
iriscli service refund-deposit --chain-id=shilei-qa --def-chain-id=shilei-qa --from=v0 --fee=0.004iris --service-name=006

     ===========================
                调用
     ===========================
10. [交易] 服务调用 (要改provider ，返回request id)
// iriscli keys show v0
echo 1234567890 | iriscli service call --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --bind-chain-id=shilei-qa --def-chain-id=shilei-qa --service-name=001  --method-id=1 --provider=faa1rqexxw5md3awqly7m34a20g05f05pc26qgy2yj --service-fee=1iris --request-data="Abcd"

--
iriscli service call --chain-id=shilei-qa --from=v0 --fee=0.004iris --bind-chain-id=shilei-qa --def-chain-id=shilei-qa --service-name=002  --method-id=1 --provider=faa1085ut6welfhcq3uzl2s7ktk67lf32u24vf9unc --service-fee=1iris --request-data="Abcd"

iriscli service call --chain-id=shilei-qa --from=v0 --fee=0.004iris --bind-chain-id=shilei-qa --def-chain-id=shilei-qa --service-name=002  --method-id=2 --provider=faa1085ut6welfhcq3uzl2s7ktk67lf32u24vf9unc --service-fee=0iris --request-data="Abcd"

--  profiler调用： --profiling=true  不收取--service-fee

echo 1234567890 | iriscli service call --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --bind-chain-id=shilei-qa --def-chain-id=shilei-qa --service-name=001  --method-id=1 --provider=faa1q99rfqlyx3s3jnfrjxlwmlztgmn8ah3wmnus5s --service-fee=100iris --request-data="Abcd" --profiling=true

====


11. 查询请求列表
iriscli service requests --def-chain-id=shilei-qa --bind-chain-id=shilei-qa --provider=faa17x5yyaul7chht952899cgchwutlc9f2uqd4335 --service-name=001
---

iriscli service requests --def-chain-id=shilei-qa --bind-chain-id=shilei-qa --provider=faa1ydg88u50qsp94v30urq0nmp99fyla3nq36lhaj --service-name=002


12. [交易] 响应服务调用
iriscli service respond --chain-id=shilei-qa --request-chain-id=shilei-qa --from=v0 --fee=0.004iris --response-data="Abcd" --request-id=177-172-0

13. 查询服务响应
iriscli service response --request-chain-id=shilei-qa  --request-id=1815-1715-0

14. 查询所有的服务费
iriscli service fees faa1kepfks8l9s5h2yrglgq2vmarrdt0v9s7e8e5e5

15. 取回服务调用退回的服务费
iriscli service refund-fees --chain-id=shilei-qa --from=v0 --fee=0.004iris

16. 取回服务调用产生的服务费
iriscli service withdraw-fees --chain-id=shilei-qa --from=v0 --fee=0.004iris

17. trustees取回tax  （这里处理的是service price所收的tax） // iriscli guardian trustees
iriscli service withdraw-tax --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --dest-address=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju --withdraw-amount=0.017iris

//iriscli bank account faa1d5e6g6fuddkndvg9nhyljwp2sya2qyk9keyu4d


    =============================
        服务快速生成
    =============================
    使用 c 2 版本
iriscli keys show v0  //faa1h27mc0zkf88vmah0kzas3erqv0j62ca6w78lzf

echo 1234567890 | iriscli service define --chain-id=shilei-qa  --from=v0 --fee=0.004iris --commit --service-description=service-description --author-description=author-description --tags=tag1,tag2 --idl-content=idl-content --file=/root/test.proto --service-name=001
echo 1234567890 | iriscli service bind --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --def-chain-id=shilei-qa --bind-type=Local --deposit=1100iris --prices=1iris --avg-rsp-time=10000 --usable-time=100 --service-name=001
[改faa]
echo 1234567890 | iriscli service call --chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --bind-chain-id=shilei-qa --def-chain-id=shilei-qa --service-name=001  --method-id=1 --provider=faa1h27mc0zkf88vmah0kzas3erqv0j62ca6w78lzf --service-fee=1iris --request-data="Abcd"
[记录id]
echo 1234567890 | iriscli service respond --chain-id=shilei-qa --request-chain-id=shilei-qa --from=v0 --fee=0.004iris --commit --response-data="Abcd" --request-id=154-134-0

iriscli service requests --def-chain-id=shilei-qa --bind-chain-id=shilei-qa --provider=faa1h27mc0zkf88vmah0kzas3erqv0j62ca6w78lzf --service-name=001

*******************************************
               9.cli config
*******************************************
gedit /root/.iriscli/config/config.toml
1.
iriscli config

*******************************************
               10.Distribution
                    iriscli distribution set-withdraw-addr
                    iriscli distribution withdraw-address

                    iriscli distribution withdraw-rewards
                        --only-from-validator
                        --is-validator=true

                    iriscli distribution delegation-distr-info (单个)
                    iriscli distribution delegator-distr-info (多个)
                    iriscli distribution validator-distr-info
*******************************************

     ===========================
                准备
     ===========================
iriscli bank send --amount=1iris-atto --to=faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju --from=v0 --fee=0.004iris --chain-id=shilei-qa
iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju

[转出所有钱，保留小数]
iriscli bank send --amount=1003iris --to=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --from=x1 --fee=0.004iris --chain-id=shilei-qa

[1000fee 自己发自己]
iriscli bank send --amount=1iris --to=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --from=v0 --gas=10000 --fee=2260.9575iris --chain-id=shilei-qa

iriscli stake validators
gedit /root/testnet/v0/iris/config/genesis.json

     ===========================
        设置withdraw地址
     ===========================
echo 1234567890 | iriscli distribution set-withdraw-addr faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --fee=0.4iris --from=v0 --chain-id=shilei-qa --commit
iriscli distribution withdraw-address faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju
iriscli bank account faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx


     ===========================
                提取reward
                iriscli keys show v0 --bech val
     ===========================
        //simulation 查询
        iriscli distribution withdraw-rewards --from=v0 --fee=0.004iris --chain-id=shilei-qa --dry-run
        iriscli distribution withdraw-rewards --is-validator=true --from=v0 --fee=0.004iris --chain-id=shilei-qa --dry-run

1. [交易]  提取reward  （无参数，提取所有的在外的rewards 不包含--is-validator=true）
echo 1234567890 | iriscli distribution withdraw-rewards --from=v0 --fee=0.004iris --commit --chain-id=shilei-qa
【验证】 iriscli bank account faa1e3tnxw802mgrjzk54m3ecgz8msw2kv4kew8hue
【查询reward 验证】 iriscli distribution validator-distr-info fva1mzzjnagmvck8fdcqqs67camw68lla0u64xgk5s --chain-id=shilei-qa

2. [交易]  提取reward  （--only-from-validator）
echo 1234567890 | iriscli distribution withdraw-rewards --only-from-validator=fva1gw77zex2u7jqguwq82dyqzpr6radznrta2zcjl --from=v0 --fee=0.004iris --chain-id=shilei-qa --commit

3. [交易]  提取reward  （ --is-validator=true） //此种方式会同时取回 reward和commission
echo 1234567890 | iriscli distribution withdraw-rewards --is-validator=true --from=v0 --fee=0.004iris --chain-id=shilei-qa
【验证】 iriscli bank account faa1lcuw6ewd2gfxap37sejewmta205sgssmv5fnju  //此时总额应该是 实际消耗fee的90%左右
【查询reward 验证】 iriscli distribution validator-distr-info fva1e3tnxw802mgrjzk54m3ecgz8msw2kv4kvldcp7 --chain-id=shilei-qa

--
iriscli distribution withdraw-rewards --is-validator=true --from=x1 --fee=0.004iris --chain-id=shilei-qa

     ===========================
                查询reward
     ===========================
6. 查询（delegator）所有的委托的【委托列表】
iriscli distribution delegator-distr-info faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx  --chain-id=shilei-qa

---
iriscli distribution delegator-distr-info faa1av59vr7pfk268vserefjyrcv0qjc9axaewuwu2  --chain-id=shilei-qa


7. 查询（delegator）在（validator）地址的【单个委托】
iriscli distribution delegation-distr-info --address-delegator=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --address-validator=fva1gw77zex2u7jqguwq82dyqzpr6radznrta2zcjl --chain-id=shilei-qa

---
iriscli distribution delegation-distr-info --address-delegator=faa1029p99sqzdeleuy3un60fm2ltm5c2vkw9tfpu5 --address-validator=fva1gw77zex2u7jqguwq82dyqzpr6radznrta2zcjl --chain-id=shilei-qa


8. 查询某个验证人 validator pool的状态， 需要提取一次才会更新。【平时有fee的话会进来一些小数，base_proposer_reward会进来，其他大部分进入global pool和community】
iriscli distribution validator-distr-info fva1tjc4x3vsnt9e05ew6ykexw3dtdrtce3sdm58j7 --chain-id=shilei-qa

--
iriscli distribution validator-distr-info fva1264n55fg25z6czs43tk6j6rs3ravkz9xqy58mp --chain-id=shilei-qa

     ===========================
      添加一个小小的delegator t1 ，方便读 validator-distr-info
     ===========================
iriscli keys add t1
iriscli bank send  --amount=2iris --to=faa1mx78jds5qghx578w36mgcflkw7tnsxtd49ua28 --from=v0 --fee=0.004iris --chain-id=shilei-qa
iriscli stake delegate --address-validator=fva1t6a5uvpjvqv3zp6rd89hzmwmtda8qzqa2veac3 --amount=0.001iris --fee=0.004iris --from=t1  --chain-id=shilei-qa
iriscli bank send  --amount=2iris --to=faa1264n55fg25z6czs43tk6j6rs3ravkz9x447gxx --from=v0 --gas=10000 --fee=2262.9575iris --chain-id=shilei-qa

iriscli distribution withdraw-rewards --from=t1 --fee=0.004iris --chain-id=shilei-qa  --node=http://10.1.2.168:26657
iriscli bank account faa1mx78jds5qghx578w36mgcflkw7tnsxtd49ua28

accum：代表update之前产出的reward所对应的工作量
iriscli distribution validator-distr-info fva1tjc4x3vsnt9e05ew6ykexw3dtdrtce3sdm58j7 --chain-id=shilei-qa



