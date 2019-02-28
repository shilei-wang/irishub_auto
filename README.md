## Iris 功能测试 结构设计 ##
![](images/1_%E6%B5%8B%E8%AF%95%E7%94%A8%E4%BE%8B%E6%A1%86%E6%9E%B6.jpg)
## Iris 测试报告 ##
![](images/2_%E8%AF%A6%E7%BB%86%E6%B5%8B%E8%AF%95%E6%8A%A5%E5%91%8A.jpg)
## Iris 功能测试 自动化框架设计 ##
![](images/3_%E7%A8%8B%E5%BA%8F%E6%A1%86%E6%9E%B6%E8%AE%BE%E8%AE%A1.jpg)
## Iris 流程设计 ##
![](images/4_%E6%B5%8B%E8%AF%95%E6%B5%81%E7%A8%8B%E5%9B%BE.jpg)

Mac  打开命令行翻墙
sudo /usr/local/sbin/privoxy /usr/local/etc/privoxy/config

proxy_on 
curl www.google.com

编译：
git fetch --all
git checkout .
git checkout -b v0.12.2-rc0
git merge upstream/v0.12.2-rc0

proxy_on 
dep ensure -v
make install

运行：
rm -rf mytestnet
iris testnet --v 1 --chain-id shilei-qa
iris start --home mytestnet/node0/iris

Auto
source scripts/setTestEnv.sh 
make install
go run /Users/zjb/mygo/src/github.com/irishub_auto/autotest-cmd/autoStart/lanchIris.go c 1
iris start --home=$HOME/testnet/v0/iris

Ledger 输入pin 00000000，进入cosmos app
iriscli keys list
iriscli keys add a --ledger

iriscli bank account faa1msdeclgja600llcav6c0nls76eg8tc3mx6w7d8
iriscli bank send --amount=10000iris --fee=0.1iris --chain-id=shilei-qa --from=v0 --to=faa1984umpzmczvrrgfnlaqhsr9jygxkvnsqytllwg --commit
iriscli bank account faa1984umpzmczvrrgfnlaqhsr9jygxkvnsqytllwg


iriscli bank send --amount=1iris --fee=0.1iris --chain-id=shilei-qa --from=x --to=faa124ngsdftl8um8zs9c83g08h8cqg37x6vyv2gx6 --commit

==========
hsm
1. install
rust: 比较慢 
curl https://sh.rustup.rs -sSf | sh

gcc:  本机已经安装
gcc --version
whereis gcc

pkg-config:
https://pkg-config.freedesktop.org/releases/pkg-config-0.29.2.tar.gz
tar -xf pkg-config-0.29.2.tar (这里没有了 .gz 下载后自动去掉了？)
cd pkg-config-0.29.2
./configure --with-internal-glib
make
sudo  make install
pkg-config --version

libusb (1.0+): (macOS (Homebrew))
sudo chown -R $(whoami) /usr/local/share/aclocal
brew install libusb 

2. tmkms:
先下载：kms
mkdir github.com
cd github.com/
git clone https://github.com/irisnet/kms.git
cd kms/

export RUSTFLAGS=-Ctarget-feature=+aes
安装：
cargo install --features yubihsm,ledgertm --path .

改名 tmkms.toml.example  ==》 tmkms.toml
复制 tmkms.toml ==》 /Users/zjb  (home目录)
修改 tmkms.toml （注意：初始密码是 key 1 password， 可按住设备10秒重置）
# Example KMS configuration file
[[validator]]
addr = "tcp://localhost:26658"    # or "unix:///path/to/socket"
chain_id = "shilei-qa"
reconnect = true # true is the default
secret_key = "/Users/zjb/.tmkms/secret_connection.key"
[[providers.yubihsm]]
adapter = { type = "usb" }
auth = { key = 1, password = "password" } # Default YubiHSM admin credentials. Change ASAP!
keys = [{ id = "test", key = 1 }]
#serial_number = "0123456789" # identify serial number of a specific YubiHSM to connect to

修改 /testnet/v0/iris/config/config.toml
# connections from an external PrivValidator process
Priv_validator_laddr = "localhost:26658"


cd ~
导入key：
把toml文件放在执行tmkms yubihsm 的目录: (密码不对会显示 memmory error，只能导入一次 第二次导入会提示已经存在)
tmkms yubihsm keys import -p ~/testnet/v0/iris/config/priv_validator.json 1  (~ 指home目录， 1指的key)

生成连接密钥secret_key: (指定在~/.tmkms/ 目录下 一般文件都会指定在 ~/.*** 中, 注意这个地址在tmkms.toml配置，配置里面只能用绝对路径)
mkdir .tmkms
tmkms keygen ~/.tmkms/secret_connection.key

启动kms：(把toml文件放在执行tmkms yubihsm 的目录)
tmkms start -c 

启动iris，正常签名出块。（注意：必须先开tmkms 再开iris， 重启后必须2个都停掉）
iris start --home=$HOME/testnet/v0/iris

