userid=$1
curl --header 'Content-Type: application/json;charset=UTF-8' \
	--request POST\
	--data-ascii '{"userid":"'$userid'", "password":"123456", "username":"cc", "sex":"male", "desc": "我是软院的小哥哥" }' \
	http://localhost:8888/user/register
read -s -n1 -p "press any key to continue... "