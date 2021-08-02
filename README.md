# 一个论坛网站-后台
spring boot + shiro + JWT + redis + restful
A community project based on Spring Boot.

## APIs
帖子列表
http://localhost:8081/community/discuss/posts?offset=3

排行榜
http://localhost:8081/community/discuss/posts?offset=3&limit=30&orderMod=1

帖子详情
http://localhost:8081/community/discuss/detail/233

发帖
http://localhost:8081/community/discuss/add

粉丝
http://localhost:8081/community/followers/101

关注者
http://localhost:8081/community/followees/101

统计信息
http://localhost:8081/community/data/uv

http://localhost:8081/community/data/dau
