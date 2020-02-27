# weather_application  

версия 2.0  
сохранен старый функционал, кроме отправки данных на прямую, минуя
почтовые клиенты, на E-Mail.   
Замена поставщика данных текущей погоды и прогнозных данных на
[openweathermap.org](https://openweathermap.org) (прогноз на 5 дней
через 3 часа).  
Запросы определения места:
- координаты текущего места (через Google Api или Android Api), 
- название города, 
- почтовый индекс места, 
- запрос на [сервис прямого геокодирования](https://opencagedata.com) 
  
Приложение пишется целиком на kotlin, используется Navigation component,
Data Binding Library, ViewModel, Room, Koin, kotlin coroutines, проект
построен на Clean Architecture, MVVM.

Проверка на утечки LeakCanary 2.2 на Api 28

*******************
###### 03.01.2019  release  v1- demo

Клиентское приложение для получения текущего состояния и прогноза погоды в выбранном месте.

В приложении используются бесплатные ключи для доступа к сервисам, имеется ограничение по количеству запросов, 
просьба использовать в демострационных целях. Спасибо.

Минимальная версия андроид 4.2(API 17)

**!!!** Не забыть разрешить установку приложений из других источников, кроме Play Market.
Если все же возникнут проблемы, отключить интернет, и попробовать снова установить из скаченного файла .apk.

Замечания, рекомендации, ошибки, пожалуйста, отсылайте на Reschikov@List.ru или воспользуйтесь обратной связью приложения. Спасибо.
********************
