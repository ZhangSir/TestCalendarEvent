package com.itzs.testcalendar;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.Permission;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnAccountAdd, btnAccountDelete, btnAccountUpdate, btnAccountQuery;

    private TextView tvAccountAdd, tvAccountDelete, tvAccountUpdate, tvAccountQuery;

    private Button btnAdd, btnDelete, btnUpdate, btnQuery;

    private TextView tvAdd, tvDelete, tvUpdate, tvQuery;

    private long tempEventId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.initView();
    }

    private void initView(){
        btnAccountAdd = (Button) this.findViewById(R.id.btn_main_account_add);
        btnAccountDelete = (Button) this.findViewById(R.id.btn_main_account_delete);
        btnAccountUpdate = (Button) this.findViewById(R.id.btn_main_account_update);
        btnAccountQuery = (Button) this.findViewById(R.id.btn_main_account_query);
        tvAccountAdd = (TextView) this.findViewById(R.id.tv_main_account_add);
        tvAccountDelete = (TextView) this.findViewById(R.id.tv_main_account_delete);
        tvAccountUpdate = (TextView) this.findViewById(R.id.tv_main_account_update);
        tvAccountQuery = (TextView) this.findViewById(R.id.tv_main_account_query);

        btnAdd = (Button) this.findViewById(R.id.btn_main_add);
        btnDelete = (Button) this.findViewById(R.id.btn_main_delete);
        btnUpdate = (Button) this.findViewById(R.id.btn_main_update);
        btnQuery = (Button) this.findViewById(R.id.btn_main_query);
        tvAdd = (TextView) this.findViewById(R.id.tv_main_add);
        tvDelete = (TextView) this.findViewById(R.id.tv_main_delete);
        tvUpdate = (TextView) this.findViewById(R.id.tv_main_update);
        tvQuery = (TextView) this.findViewById(R.id.tv_main_query);

        btnAccountAdd.setOnClickListener(this);
        btnAccountDelete.setOnClickListener(this);
        btnAccountUpdate.setOnClickListener(this);
        btnAccountQuery.setOnClickListener(this);

        btnAdd.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        btnQuery.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_main_account_add:
                tvAccountAdd.setText(insertAccount());
                break;
            case R.id.btn_main_account_delete:
                tvAccountDelete.setText(deleteAccount());
                break;
            case R.id.btn_main_account_update:
                tvAccountUpdate.setText(updateAccount());
                break;
            case R.id.btn_main_account_query:
                tvAccountQuery.setText(queryAccount());
                break;
            case R.id.btn_main_add:
                long calID = 6;//日历账户id
                tvAdd.setText(insertEvent(calID));
                break;
            case R.id.btn_main_delete:
                tvDelete.setText(deleteEvent(tempEventId));
                break;
            case R.id.btn_main_update:
                tvUpdate.setText(updateEvent(tempEventId));
                break;
            case R.id.btn_main_query:
                long calID1 = 6;//日历账户id
                tvQuery.setText(queryEvent(calID1));
                break;
        }
    }

    /**
     * 插入一条新的日历账户
     * @return
     */
    private String insertAccount(){
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        Uri accountUri = null;

        String accountName = "测试AccountName";

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        //在添加账户时，如果账户类型不存在系统中，则可能该新增记录会被标记为脏数据而被删除，设置为ACCOUNT_TYPE_LOCAL可以保证在不存在账户类型时，该新增数据不会被删除；
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, "name");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "测试账户");
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0XFF5555);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "测试OwnerAccount");
        values.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1);
        values.put(CalendarContract.Calendars.MAX_REMINDERS, 8);
        values.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,4");
        values.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2");
        values.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "0,1,2,3");
//        values.put(CalendarContract.Calendars.IS_PRIMARY, 1);
//        values.put(CalendarContract.Calendars.DIRTY, 1);

        //修改或添加ACCOUNT_NAME只能由syn-adapter调用，对uri设置CalendarContract.CALLER_IS_SYNCADAPTER为true，即标记当前操作为syn_adapter操作；
        //再设置CalendarContract.CALLER_IS_SYNCADAPTER为true时，必须带上参数ACCOUNT_NAME和ACCOUNT_TYPE，至于这两个参数的值，可以随意填，不影响最终结果；
        uri = uri
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "Test")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                accountUri = cr.insert(uri, values);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            accountUri = cr.insert(uri, values);
        }
        // get the event ID that is the last element in the Uri
        long accountID = Long.parseLong(accountUri.getLastPathSegment());

        sb.append("新增日历账户成功！\n");
        sb.append("accountID:" + accountID).append("\n");
        sb.append("accountName:" + accountName).append("\n");
        sb.append("\n");
        return sb.toString();
    }

    /**
     * 删除日历账户
     * @return
     */
    private String deleteAccount(){
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        int deletedCount = 0;

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{"测试AccountName", CalendarContract.ACCOUNT_TYPE_LOCAL};

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                deletedCount = cr.delete(uri, selection, selectionArgs);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            deletedCount = cr.delete(uri, selection, selectionArgs);
        }
        sb.append("删除日历账户成功！\n");
        sb.append("删除账户数:" + deletedCount).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * 更新日历账户
     * @return
     */
    private String updateAccount(){
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        int updatedCount = 0;

        String accountName = "更新测试AccountName";

        ContentValues values = new ContentValues();
        values.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        //在添加账户时，如果账户类型不存在系统中，则可能该新增记录会被标记为脏数据而被删除，设置为ACCOUNT_TYPE_LOCAL可以保证在不存在账户类型时，该新增数据不会被删除；
        values.put(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL);
        values.put(CalendarContract.Calendars.NAME, "更新name");
        values.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, "更新测试账户");
        values.put(CalendarContract.Calendars.CALENDAR_COLOR, 0X55FF55);
        values.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        values.put(CalendarContract.Calendars.VISIBLE, 1);
        values.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE, 1);
        values.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        values.put(CalendarContract.Calendars.OWNER_ACCOUNT, "更新测试OwnerAccount");
        values.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 1);
        values.put(CalendarContract.Calendars.MAX_REMINDERS, 8);
        values.put(CalendarContract.Calendars.ALLOWED_REMINDERS, "0,1,4");
        values.put(CalendarContract.Calendars.ALLOWED_AVAILABILITY, "0,1,2");
        values.put(CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES, "0,1,2,3");
        values.put(CalendarContract.Calendars.IS_PRIMARY, 1);
        values.put(CalendarContract.Calendars.DIRTY, 1);

        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
        String[] selectionArgs = new String[]{"测试AccountName", CalendarContract.ACCOUNT_TYPE_LOCAL};

        //修改或添加ACCOUNT_NAME只能由syn-adapter调用，对uri设置CalendarContract.CALLER_IS_SYNCADAPTER为true，即标记当前操作为syn_adapter操作；
        //再设置CalendarContract.CALLER_IS_SYNCADAPTER为true时，必须带上参数ACCOUNT_NAME和ACCOUNT_TYPE，至于这两个参数的值，可以随意填，不影响最终结果；
        uri = uri
                .buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, "Test")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CalendarContract.ACCOUNT_TYPE_LOCAL)
                .build();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                updatedCount = cr.update(uri, values, selection, selectionArgs);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            updatedCount = cr.update(uri, values, selection, selectionArgs);
        }

        sb.append("更新日历账户成功！\n");
        sb.append("更新账户数:" + updatedCount).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * 查询所有的日历账户
     * @return
     */
    private String queryAccount() {
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Calendars._ID,                           // 0
                CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,          // 2
                CalendarContract.Calendars.CALENDAR_COLOR,                  //3
                CalendarContract.Calendars.CALENDAR_COLOR_KEY,              //4
                CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL,           //5
                CalendarContract.Calendars.VISIBLE,                         //6
                CalendarContract.Calendars.CALENDAR_TIME_ZONE,              //7
                CalendarContract.Calendars.SYNC_EVENTS,                     //8
                CalendarContract.Calendars.OWNER_ACCOUNT,                   //9
                CalendarContract.Calendars.CAN_ORGANIZER_RESPOND,           //10
                CalendarContract.Calendars.CAN_MODIFY_TIME_ZONE,            //11
                CalendarContract.Calendars.MAX_REMINDERS,                   //12
                CalendarContract.Calendars.ALLOWED_REMINDERS,               //13
                CalendarContract.Calendars.ALLOWED_AVAILABILITY,            //14
                CalendarContract.Calendars.ALLOWED_ATTENDEE_TYPES,          //15
                CalendarContract.Calendars.IS_PRIMARY,                       //16
                CalendarContract.Calendars.ACCOUNT_TYPE,                  // 17
                CalendarContract.Calendars.NAME,                  // 18
                CalendarContract.Calendars.DIRTY                  // 19

        };
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;

        StringBuilder sb = new StringBuilder();

//        String selection = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND ("
//                + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";
//        String[] selectionArgs = new String[]{"sampleuser@gmail.com", "com.google"};

        String selection = null;
        String[] selectionArgs = null;

        // Submit the query and get a Cursor object back.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.READ_CALENDAR")){
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            }else{
                return "请授予读取日历的权限";
            }
        }else{
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }


        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long calID = 0;
            String displayName = null;
            String accountName = null;
            int calendarColor = 0;
            int calendarAccessLevel = 0;
            int visible = 0;
            String calendarTimezone = null;
            int syncEvents = 0;
            String ownerAccount = null;
            int canOrganizerRespond = 0;
            int maxReminders = 0;
            String allowedReminders = null;
            String allowedAvailability = null;
            String allowedAttendeeTypes = null;
            int isPrimary = 0;
            String accountType = null;
            String name = null;
            int dirty = 0;

            // Get the field values
            calID = cur.getLong(0);
            accountName = cur.getString(1);
            displayName = cur.getString(2);
            calendarColor = cur.getInt(3);
            calendarAccessLevel = cur.getInt(5);
            visible = cur.getInt(6);
            calendarTimezone = cur.getString(7);
            syncEvents = cur.getInt(8);
            ownerAccount = cur.getString(9);
            canOrganizerRespond = cur.getInt(10);
            maxReminders = cur.getInt(12);
            allowedReminders = cur.getString(13);
            allowedAvailability = cur.getString(14);
            allowedAttendeeTypes = cur.getString(15);
            isPrimary = cur.getInt(16);
            accountType = cur.getString(17);
            name = cur.getString(18);
            dirty = cur.getInt(19);

            sb.append("calID:" + calID).append("\n");
            sb.append("accountName:" + accountName).append("\n");
            sb.append("accountType:" + accountType).append("\n");
            sb.append("name:" + name).append("\n");
            sb.append("displayName:" + displayName).append("\n");
            sb.append("calendarColor:" + calendarColor).append("\n");
            sb.append("calendarAccessLevel:" + calendarAccessLevel).append("\n");
            sb.append("visible:" + visible).append("\n");
            sb.append("calendarTimezone:" + calendarTimezone).append("\n");
            sb.append("syncEvents:" + syncEvents).append("\n");
            sb.append("ownerAccount:" + ownerAccount).append("\n");
            sb.append("canOrganizerRespond:" + canOrganizerRespond).append("\n");
            sb.append("maxReminders:" + maxReminders).append("\n");
            sb.append("allowedReminders:" + allowedReminders).append("\n");
            sb.append("allowedAvailability:" + allowedAvailability).append("\n");
            sb.append("allowedAttendeeTypes:" + allowedAttendeeTypes).append("\n");
            sb.append("isPrimary:" + isPrimary).append("\n");
            sb.append("dirty:" + dirty).append("\n");
            sb.append("\n");

        }
        cur.close();
        return  sb.toString();
    }


    /**
     * 插入日历事件及提醒
     * @param calID 插入日历事件的账户
     * @return
     */
    private String insertEvent(long calID){
        /*
        以下是针对插入一个新的事件的一些规则：
        1.  必须包含CALENDAR_ID和DTSTART字段
        2.  必须包含EVENT_TIMEZONE字段。使用getAvailableIDs()方法获得系统已安装的时区ID列表。注意如果通过INSTERT类型Intent对象来插入事件，那么这个规则不适用，因为在INSERT对象的场景中会提供一个默认的时区；
        3.  对于非重复发生的事件，必须包含DTEND字段；
        4.  对重复发生的事件，必须包含一个附加了RRULE或RDATE字段的DURATIION字段。注意，如果通过INSERT类型的Intent对象来插入一个事件，这个规则不适用。因为在这个Intent对象的应用场景中，你能够把RRULE、DTSTART和DTEND字段联合在一起使用，并且Calendar应用程序能够自动的把它转换成一个持续的时间。
         */
        long startMillis = 0;
        long endMillis = 0;
        Uri uri = CalendarContract.Events.CONTENT_URI;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 3, 29, 16, 15);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 3, 29, 16, 20);
        endMillis = endTime.getTimeInMillis();
        Uri eventUri = null;
        StringBuilder sb = new StringBuilder();

        String title = "测试日历事件";

        ContentResolver cr = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "有一次Android进阶讲座，快去职学堂APP");
        values.put(CalendarContract.Events.EVENT_LOCATION, "新模范马路");
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
        values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.ORGANIZER, "499083701@qq.com");
        values.put(CalendarContract.Events.STATUS, 1);
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        values.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                eventUri = cr.insert(uri, values);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            eventUri = cr.insert(uri, values);
        }
        // get the event ID that is the last element in the Uri
        long eventID = Long.parseLong(eventUri.getLastPathSegment());

        // 事件提醒的设定
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminderValues.put(CalendarContract.Reminders.MINUTES, 10);// 提前10分钟有提醒
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);// 提醒方式
        Uri reminderUri = cr.insert(CalendarContract.Reminders.CONTENT_URI, reminderValues);
        long reminderID = Long.parseLong(reminderUri.getLastPathSegment());

        sb.append("添加日历事件成功！\n");
        sb.append("eventID:" + eventID).append("\n");
        sb.append("reminderID:" + reminderID).append("\n");
        sb.append("calID:" + calID).append("\n");
        sb.append("title:" + title).append("\n");
        sb.append("\n");
        tempEventId = eventID;
        return sb.toString();
    }

    /**
     * 删除指定ID的日历事件及其提醒
     * @param eventId
     * @return
     */
    private String deleteEvent(long eventId){
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        int deletedCount = 0;

        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(eventId)};

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                deletedCount = cr.delete(uri, selection, selectionArgs);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            deletedCount = cr.delete(uri, selection, selectionArgs);
        }

        String reminderSelection = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)";
        String[] reminderSelectionArgs = new String[]{String.valueOf(eventId)};

        int deletedReminderCount = cr.delete(CalendarContract.Reminders.CONTENT_URI, reminderSelection, reminderSelectionArgs);

        sb.append("删除日历事件成功！\n");
        sb.append("删除事件数:" + deletedCount).append("\n");
        sb.append("删除事件提醒数:" + deletedReminderCount).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * 更新指定ID的日历事件及其提醒
     * @param eventID
     * @return
     */
    private String updateEvent(long eventID){
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;
        StringBuilder sb = new StringBuilder();
        int updatedCount = 0;

        String title = "更新测试日历事件";

        long startMillis = 0;
        long endMillis = 0;
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 4, 29, 16, 15);
        startMillis = beginTime.getTimeInMillis();
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 4, 29, 16, 20);
        endMillis = endTime.getTimeInMillis();

        ContentValues values = new ContentValues();
//        values.put(CalendarContract.Events.CALENDAR_ID, calID);
        values.put(CalendarContract.Events.DTSTART, startMillis);
        values.put(CalendarContract.Events.DTEND, endMillis);
        values.put(CalendarContract.Events.TITLE, title);
        values.put(CalendarContract.Events.DESCRIPTION, "更新有一次Android进阶讲座，快去职学堂APP");
        values.put(CalendarContract.Events.EVENT_LOCATION, "更新新模范马路");
        values.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());
        values.put(CalendarContract.Events.ACCESS_LEVEL, CalendarContract.Events.ACCESS_DEFAULT);
        values.put(CalendarContract.Events.ALL_DAY, 1);
        values.put(CalendarContract.Events.ORGANIZER, "499083701@qq.com");
        values.put(CalendarContract.Events.STATUS, 1);
        values.put(CalendarContract.Events.HAS_ALARM, 1);
        values.put(CalendarContract.Events.HAS_ATTENDEE_DATA, 1);

        String selection = "(" + CalendarContract.Events._ID + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(eventID)};

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.WRITE_CALENDAR")){
                updatedCount = cr.update(uri, values, selection, selectionArgs);
            }else{
                return "请授予编辑日历的权限";
            }
        }else{
            updatedCount = cr.update(uri, values, selection, selectionArgs);
        }

        // 事件提醒的设定
        ContentValues reminderValues = new ContentValues();
        reminderValues.put(CalendarContract.Reminders.MINUTES, 20);// 提前10分钟有提醒
        reminderValues.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);// 提醒方式

        String reminderSelection = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)";
        String[] reminderSelectionArgs = new String[]{String.valueOf(eventID)};

        int updatedReminderCount = cr.update(CalendarContract.Reminders.CONTENT_URI, reminderValues, reminderSelection, reminderSelectionArgs);

        sb.append("更新日历事件成功！\n");
        sb.append("更新事件数:" + updatedCount).append("\n");
        sb.append("更新事件提醒数:" + updatedReminderCount).append("\n");
        sb.append("\n");

        return sb.toString();
    }

    /**
     * 查询指定日历账户下的日历事件及提醒
     * @param calendarID
     * @return
     */
    private String queryEvent(long calendarID) {
        String[] EVENT_PROJECTION = new String[]{
                CalendarContract.Events.CALENDAR_ID,                           // 0
                CalendarContract.Events.TITLE,                  // 1
                CalendarContract.Events.DESCRIPTION,          // 2
                CalendarContract.Events.EVENT_LOCATION,                  //3
                CalendarContract.Events.DISPLAY_COLOR,              //4
                CalendarContract.Events.STATUS,           //5
                CalendarContract.Events.DTSTART,                         //6
                CalendarContract.Events.DTEND,              //7
                CalendarContract.Events.DURATION,                     //8
                CalendarContract.Events.EVENT_TIMEZONE,                   //9
                CalendarContract.Events.EVENT_END_TIMEZONE,           //10
                CalendarContract.Events.ALL_DAY,            //11
                CalendarContract.Events.ACCESS_LEVEL,                   //12
                CalendarContract.Events.AVAILABILITY,               //13
                CalendarContract.Events.HAS_ALARM,            //14
                CalendarContract.Events.RRULE,          //15
                CalendarContract.Events.RDATE,                       //16
                CalendarContract.Events.HAS_ATTENDEE_DATA,                  // 17
                CalendarContract.Events.LAST_DATE,                  // 18
                CalendarContract.Events.ORGANIZER,                  // 19
                CalendarContract.Events.IS_ORGANIZER,                  // 20
                CalendarContract.Events._ID                         //21

        };
        // Run query
        Cursor cur = null;
        ContentResolver cr = getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        StringBuilder sb = new StringBuilder();

        String selection = "(" + CalendarContract.Events.CALENDAR_ID + " = ?)";
        String[] selectionArgs = new String[]{String.valueOf(calendarID)};

//        String selection = null;
//        String[] selectionArgs = null;

        // Submit the query and get a Cursor object back.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(PackageManager.PERMISSION_GRANTED == checkSelfPermission("android.permission.READ_CALENDAR")){
                cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
            }else{
                return "请授予读取日历的权限";
            }
        }else{
            cur = cr.query(uri, EVENT_PROJECTION, selection, selectionArgs, null);
        }


        // Use the cursor to step through the returned records
        while (cur.moveToNext()) {
            long id = 0;
            long calID = 0;
            String title = null;
            String description = null;
            String eventLocation = null;
            int displayColor = 0;
            int status = 0;
            long start = 0;
            long end = 0;
            String duration = null;
            String eventTimeZone = null;
            String eventEndTimeZone = null;
            int allDay = 0;
            int accessLevel = 0;
            int availability = 0;
            int hasAlarm = 0;
            String rrule = null;
            String rdate = null;
            int hasAttendeeData = 0;
            int lastDate;
            String organizer;
            String isOrganizer;

            // Get the field values
            id = cur.getLong(21);
            calID = cur.getLong(0);
            title = cur.getString(1);
            description = cur.getString(2);
            eventLocation = cur.getString(3);
            displayColor = cur.getInt(4);
            status = cur.getInt(5);
            start = cur.getLong(6);
            end = cur.getLong(7);
            duration = cur.getString(8);
            eventTimeZone = cur.getString(9);
            eventEndTimeZone = cur.getString(10);
            allDay = cur.getInt(11);
            accessLevel = cur.getInt(12);
            availability = cur.getInt(13);
            hasAlarm = cur.getInt(14);
            rrule = cur.getString(15);
            rdate = cur.getString(16);
            hasAttendeeData = cur.getInt(17);
            lastDate = cur.getInt(18);
            organizer = cur.getString(19);
            isOrganizer = cur.getString(20);

            sb.append("id:" + id).append("\n");
            sb.append("calID:" + calID).append("\n");
            sb.append("title:" + title).append("\n");
            sb.append("description:" + description).append("\n");
            sb.append("eventLocation:" + eventLocation).append("\n");
            sb.append("displayColor:" + displayColor).append("\n");
            sb.append("status:" + status).append("\n");
            sb.append("start:" + start).append("\n");
            sb.append("end:" + end).append("\n");
            sb.append("duration:" + duration).append("\n");
            sb.append("eventTimeZone:" + eventTimeZone).append("\n");
            sb.append("eventEndTimeZone:" + eventEndTimeZone).append("\n");
            sb.append("allDay:" + allDay).append("\n");
            sb.append("accessLevel:" + accessLevel).append("\n");
            sb.append("availability:" + availability).append("\n");
            sb.append("hasAlarm:" + hasAlarm).append("\n");
            sb.append("rrule:" + rrule).append("\n");
            sb.append("rdate:" + rdate).append("\n");
            sb.append("hasAttendeeData:" + hasAttendeeData).append("\n");
            sb.append("lastDate:" + lastDate).append("\n");
            sb.append("organizer:" + organizer).append("\n");
            sb.append("isOrganizer:" + isOrganizer).append("\n");
            sb.append("------------------\n");


            String[] REMINDER_PROJECTION = new String[]{
                    CalendarContract.Reminders._ID,                           // 0
                    CalendarContract.Reminders.EVENT_ID,                // 1
                    CalendarContract.Reminders.MINUTES,                  //2
                    CalendarContract.Reminders.METHOD,                  //3
            };
            String reminderSelection = "(" + CalendarContract.Reminders.EVENT_ID + " = ?)";
            String[] reminderSelectionArgs = new String[]{String.valueOf(id)};

            Cursor reminderCur = cr.query(CalendarContract.Reminders.CONTENT_URI, REMINDER_PROJECTION, reminderSelection, reminderSelectionArgs, null);

            while (reminderCur.moveToNext()){
                long reminderId = 0;
                long reminderEventID = 0;
                int reminderMinute = 0;
                int reminderMethod = 0;

                reminderId = reminderCur.getLong(0);
                reminderEventID = reminderCur.getLong(1);
                reminderMinute = reminderCur.getInt(2);
                reminderMethod = reminderCur.getInt(3);

                sb.append("reminderId:" + reminderId).append("\n");
                sb.append("reminderEventID:" + reminderEventID).append("\n");
                sb.append("reminderMinute:" + reminderMinute).append("\n");
                sb.append("reminderMethod:" + reminderMethod).append("\n");
                sb.append("\n");
            }



        }
        cur.close();
        return  sb.toString();
    }
}
