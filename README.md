# 论坛社区-后端
## 简介
基本的社区论坛后端，实现邮箱注册及验证、登录、发帖、评论、私信、系统通知、点赞、关注、权限控制、数据统计等功能。
## 技术栈
1. springboot
2. MySQL
3. Redis
4. kafka
5. shiro
6. JWT




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
