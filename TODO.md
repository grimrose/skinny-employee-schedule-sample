### TODO

* employee and schedule

    * EmployeesSchedulesControllerを分割する

        * 関連付ける処理と検索する処理を分ける。
    
    * ScheduleをEmployeeに関連付ける機能を追加する。
    
        * Scheduleを選択して、複数のEmployeeに関連付ける。

    * SQLに関するExceptionでRetryを促せるものは促すようにする。
    
        * Errorになった場合のControllerの処理
   
* テストケースの追加。

* i18n化(日本語と英語の切り替え)

* travis-ci の設定をする。

* jasmine

    * scala.js test

* 機能追加

    * 検索
        * employees
        * schedule
        * planned schedule