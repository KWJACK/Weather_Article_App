# Introduction

사용자 입력 주소에 따른 동네 날씨 예보와 뉴스정보를 한 app에서 알려주는 OneTouch app

# Features

- 기상청에서 제공하는 동네 예보를 출력. 만약 예보에 비소식이 있다면 notification을 통해 강수 예정 알림 제공, 예보에 비 소식이 하나도 없으면 화창하다는 notification 제공
- 주소는 사용자가 직접 입력해서 갖고 올 수 있고, 사용을 위해 GPS를 사용하지않고도google에서 제공하는 기능을 통해 위도, 경도로 변환, 이에 대응하는 기상청 동네 예보를 출력
- 사용자가 주소를 등록해 놓으면 주소 입력 없이도 등록한 주소의 날씨 조회 가능. 주소 등록은 자유롭게 가능
- 오늘 날짜, 현재 시각 표시
- 3분 이내의 뉴스 동영상을 시청할 수 있도록 SBS 8뉴스 웹 링크로 손쉽게 이동
- 최근 순으로 분류된 SBS 종합 뉴스 15건을 파싱하여 스크롤 뷰에서 제공. 각각의 아이템 클릭 시 해당 기사의 링크로 이동 가능

# Architecture

![architecture](https://cloud.githubusercontent.com/assets/20148930/23981389/8fea4784-0a49-11e7-98d1-4f92862509e1.png)

# Overview

![overview](https://cloud.githubusercontent.com/assets/20148930/23981396/9f8d8d40-0a49-11e7-913a-f93177c66a62.png)
