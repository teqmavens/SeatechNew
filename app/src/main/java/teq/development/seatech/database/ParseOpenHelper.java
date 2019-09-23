package teq.development.seatech.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by online computers on 11/19/2015.
 */
public class ParseOpenHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "SEATECH";
    public static final int VERSION = 6;
    private static ParseOpenHelper mInstance = null;
    public static final String TABLENAME_ALLJOBS = "alljobs";
    public static final String TABLENAME_ALLJOBSCURRENTDAY = "alljobscurrentDay";
    public static final String TABLE_PICKUPJOBS = "tablepickUpJobs";
    public static final String TABLENAME_MANUFACTURER = "tablemanufacturer";
    public static final String TABLENAME_NEEDESTIMATE = "tableneedestimate";
    public static final String TABLE_SUBMITMYLABOR_NEWOFFRECORD = "tablesubmitmylabornewoffrecord";
    public static final String TABLE_LCCHANGE = "tablelcchange";
    public static final String TABLE_JOBSTATUS = "tablejobstatus";
    public static final String TABLE_UPLOADIMAGES = "tableuploadimages";
    public static final String TABLE_ADDPART = "tableaddpart";
    public static final String TABLE_URGENTMSG = "tableurgentmsg";
    public static final String TABLE_NOTIFICATIONS = "tablenotifications";
    public static final String TABLE_CHATPARENTLEFT = "tablechatparentleft";
    public static final String TABLE_CHATMSGS = "tablechatmsgs";
    public static final String TABLE_SCHEDULEFILTER = "tableschedulefilter";
    public static final String TABLE_SCHEDULEDATA = "tablescheduledata";
    public static final String TABLE_SCHEDULECALENDARDATA = "tablecalendardata";
    public static final String TABLE_SCHEDULEWEEKDATA = "tableweekdata";
    public static final String TABLE_SCHEDULEDAYDATA = "tabledaydata";
    public static final String TABLE_STARTJOB = "tablestartjob";


    //All jobs table keys
    public static final String TECHID = "techid";
    public static final String JOBID = "jobid";
    public static final String JOBSSKELETON = "jobsskeleton";
    public static final String JOBSTECHDASHBOARDNOTES = "jobstechdashboardnotes";
    public static final String JOBSTECHLABORPERFORM = "jobstechlaborperform";
    public static final String JOBSTECHOFFTHERECORD = "jobstechofftherecord";
    public static final String JOBSTECHUPLOADEDIMAGES = "jobstechuploadedimages";
    public static final String JOBSTECHPARTSRECORD = "jobstechpartsrecord";
    public static final String JOBSTECHLCRECORD = "jobstechlcrecord";

    //Current Day All jobs table keys
    public static final String TECHIDCURRDAY = "techidcurrday";
    public static final String JOBIDCURRDAY = "jobidcurrday";
    public static final String JOBSSKELETONCURRDAY = "jobsskeletoncurrday";
    public static final String JOBSSKELETONCURRDAY_DATE = "jobsskeletoncurrday_date";
    public static final String JOBSTECHDASHBOARDNOTESCURRDAY = "jobstechdashboardnotescurrday";
    public static final String JOBSTECHLABORPERFORMCURRDAY = "jobstechlaborperformcurrday";
    public static final String JOBSTECHOFFTHERECORDCURRDAY = "jobstechofftherecordcurrday";
    public static final String JOBSTECHUPLOADEDIMAGESCURRDAY = "jobstechuploadedimagescurrday";
    public static final String JOBSTECHPARTSRECORDCURRDAY = "jobstechpartsrecordcurrday";
    public static final String JOBSTECHLCRECORDCURRDAY = "jobstechlcrecordcurrday";
    // public static final String JOBSTECHURGENTMSGCURRDAY = "jobstechurgentmsgcurrday";

    //All Manufacturer table key
    public static final String ALLMANUFACTURER = "allmaufacturer";

    // PickUpJobs Key
    public static final String PICKUPTECHID = "PICKUPtechid";
    public static final String PICKUPCUSTOMERNAME = "PICKUPcustomername";
    public static final String PICKUPJOBID = "PICKUPjobid";
    public static final String PICKUPCUSTOMERTYPENAME = "PICKUPcustomertypename";
    public static final String PICKUPREGIONNAME = "PICKUPregionName";
    public static final String PICKUPESTIMATEDHR = "PICKUPestimatedhr";


    //Need Estimate table key
    public static final String ESTIMATECREATEDAT = "estimatecreatedat";
    public static final String ESTIMATETECHID = "estimatetechid";
    public static final String ESTIMATEJOBID = "estimatejobid";
    public static final String ESTIMATEDESCRIPTION = "estimatedescription";
    public static final String ESTIMATEURGENT = "estimateurgent";

    // Submit mylabor and new off record Key
    public static final String SUBMITLABORTECHID = "SUBMITLABORtechid";
    public static final String SUBMITLABORJOBID = "SUBMITLABORjobid";
    public static final String SUBMITLABORTYPE = "SUBMITLABORtype";
    public static final String SUBMITLABORTIME = "SUBMITLABORtime";
    public static final String SUBMITLABORNOTES = "SUBMITLABORnotes";
    public static final String SUBMITLABORREST = "SUBMITLABORrest";

    // Submit LaborCode with timing Key
    public static final String LCCHANGETECHID = "LCCHANGEtechid";
    public static final String LCCHANGEJOBID = "LCCHANGEjobid";
    public static final String LCCHANGELC = "LCCHANGElc";
    public static final String LCCHANGESTARTTIME = "LCCHANGEstarttime";
    public static final String LCCHANGEENDTIME = "LCCHANGEendtime";
    public static final String LCCHANGEHHOURS = "LCCHANGEhours";
    public static final String LCCHANGEHHOURSADJUSTED = "LCCHANGEhoursAdjusted";
    public static final String LCCHANGECREATEDBY = "LCCHANGEcreatedby";
    public static final String LCCHANGECREATEDAT = "LCCHANGEcreatedAt";
    public static final String LCCHANGECOUNT = "LCCHANGEcount";

    // Submit JobStatus with timing Key
    public static final String JOBSTATUSTECHID = "JOBSTATUStechid";
    public static final String JOBSTATUSJOBID = "JOBSTATUSjobid";
    public static final String JOBSTATUSCOMPLETED = "JOBSTATUScompleted";
    public static final String JOBSTATUSCAPTAINAWARE = "JOBSTATUScaptionaware";
    public static final String JOBSTATUSNOTES = "JOBSTATUSnotes";
    public static final String JOBSTATUSSUPPLAYAMT = "JOBSTATUSsupplyAmount";
    public static final String JOBSTATUSNBILLABLEHR = "JOBSTATUSnbillablehrs";
    public static final String JOBSTATUSNBILLABLEHRDESC = "JOBSTATUSnbillablehrsDesc";
    public static final String JOBSTATUSRETURNIMMED = "JOBSTATUSreturnimmid";
    public static final String JOBSTATUSALREADYSCHEDULED = "JOBSTATUSalreadyScheduled";
    public static final String JOBSTATUSREASON = "JOBSTATUSreason";
    public static final String JOBSTATUSDESCRIPTION = "JOBSTATUSdescription";
    public static final String JOBSTATUSSTARTTIME = "JOBSTATUSstart_T";
    public static final String JOBSTATUSENDTTIME = "JOBSTATUSend_T";
    public static final String JOBSTATUSHOURS = "JOBSTATUShours";
    public static final String JOBSTATUSHOURSADJUSTED = "JOBSTATUShours_adjusted";
    public static final String JOBSTATUSHOURSADJUSTEDEND = "JOBSTATUShours_adjustedend";
    public static final String JOBSTATUSLABOURCODE = "JOBSTATUlabour_code";
    public static final String JOBSTATUSCREATEDAT = "JOBSTATUCreatedAt";
    public static final String JOBSTATUSBOATNAME = "JOBSTATUBoatname";
    public static final String JOBSTATUSHULLID = "JOBSTATUHullid";
    public static final String JOBSTATUSCAPTIONNAME = "JOBSTATUCaptionname";
    public static final String JOBSTATUSCOUNT = "JOBSTATUScount";


    // Submit mylabor and new off record Key
    public static final String UPLOADIMAGESCREATEDBY = "UPLOADIMAGEScreatedBy";
    public static final String UPLOADIMAGESJOBID = "UPLOADIMAGESjobid";
    public static final String UPLOADIMAGESDESCR = "UPLOADIMAGESDescr";
    public static final String UPLOADIMAGESCREATEDAT = "UPLOADIMAGESCreatedAt";
    public static final String UPLOADIMAGESALLIMAGE = "UPLOADIMAGESallimages";


    // Submit AddPart with timing Key
    public static final String ADDPARTCOUNT = "ADDPARTcount";
    public static final String ADDPARTTECHID = "ADDPARTtechid";
    public static final String ADDPARTJOBID = "ADDPARTjobid";
    public static final String ADDPARTURGENT = "ADDPARTurgent";
    public static final String ADDPARTDESCRIPTION = "ADDPARTdescription";
    public static final String ADDPARTHOWFASTNEEDED = "ADDPARThowFastNeeded";
    public static final String ADDPARTPRICEAPPROVALREQUIRED = "ADDPARTpriceapprovalRequired";
    public static final String ADDPARTFORREPAIR = "ADDPARTforRepair";
    public static final String ADDPARTMANUFACTID = "ADDPARTmanufacid";
    public static final String ADDPARTNO = "ADDPARTno";
    public static final String ADDPARTQUANTITYNEEDED = "ADDPARTquantityneeded";
    public static final String ADDPARTSERIALNO = "ADDPARTserialNo";
    public static final String ADDPARTFAILUREDESC = "ADDPARTfailuredesc";
    public static final String ADDPARTTECHSUPPORTNAME = "ADDPARTtechSupportName";
    public static final String ADDPARTSETRMAORCASE = "ADDPARTsetRmaorCase";
    public static final String ADDPARTMFGDEEMTHISWARRANTY = "ADDPARTmfgDeemThisWarranty";
    public static final String ADDPARTADVANCEREPLACEMENT = "ADDPARTadvanceReplacement";
    public static final String ADDPARTSOLDBYSEATECH = "ADDPARTSoldBySeatech";
    public static final String ADDPARTNEEDLOANER = "ADDPARTNeedLoaner";
    public static final String ADDPARTNOTES = "ADDPARTNotes";
    public static final String ADDPARTCREATEDAT = "ADDPARTCreatedAt";
    public static final String ADDPARTUPLOADEDIMAGES = "ADDPARTUploadedIMages";

    // Urgent Messages Key
    public static final String URGENT_TECHID = "URGENTtechid";
    public static final String URGENT_JOBID = "URGENTjobid";
    public static final String URGENT_CUSTNAME = "URGENT_custname";
    public static final String URGENT_CUSTTYPE = "URGENT_custtype";
    public static final String URGENT_BOATMKYEAR = "URGENT_boatmkyear";
    public static final String URGENT_BOATNAME = "URGENT_boatname";
    public static final String URGENT_SENDER = "URGENT_Sender";
    public static final String URGENT_RECEIVER = "URGENT_Receiver";
    public static final String URGENT_MESSAGE = "URGENT_message";
    public static final String URGENT_CREATEDAT = "URGENT_createdat";
    public static final String URGENT_ACKNOWLEDGE = "URGENT_acknowledge";
    public static final String URGENT_MESSAGEID = "URGENT_messageid";
    public static final String URGENT_RECEIVERID = "URGENT_receiverid";

    // Notifications Key
    public static final String NOTIFICATION_TECHID = "NOTItechid";
    public static final String NOTIFICATION_REST = "NOTIrest";

    //chat parentleft table keys
    public static final String CHATPARENT_TEQID = "CHATparent_teqid";
    public static final String CHATPARENT_JOBID = "CHATparent_jobid";
    public static final String CHATPARENT_CUSNAME = "CHATparent_cusname";
    public static final String CHATPARENT_CUSTYPE = "CHATparent_custype";
    public static final String CHATPARENT_BMY = "CHATparent_bmy";
    public static final String CHATPARENT_BNAME = "CHATparent_bname";
    public static final String CHATPARENT_NEWMSG = "CHATparent_newmsg";

    //chat msgs table keys
    public static final String CHAT_TEQID = "CHAT_teqid";
    public static final String CHAT_JOBID = "CHAT_jobid";
    public static final String CHAT_REST = "CHAT_rest";

    //schedule fileter table keys
    public static final String SCHEDULEFILTER_REGIONDATA = "regionData";
    public static final String SCHEDULEFILTER_TECHDATA = "technicianData";
    public static final String SCHEDULEFILTER_JOBSDATA = "jobsData";
    public static final String SCHEDULEFILTER_CUSTOMERDATA = "customerData";

    //schedule timeline data table keys
    public static final String SCHEDULEDDATA = "scheduledData";

    //schedule Calendar data table keys
    public static final String SCHEDULECALENDARDATA = "schedulecalendarData";

    //schedule Week data table keys
    public static final String SCHEDULEWEEKDATA = "scheduleweekData";

    //schedule Day data table keys
    public static final String SCHEDULEDAYDATA = "scheduledayData";

    //Start job table keys
    public static final String ISJOBSTARTED = "isjobstarted";
    public static final String JOBSTARTED_JOBID = "jobstartedjobid";


    public synchronized static ParseOpenHelper getInstance(Context ctx) {
        /**
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://android-developers.blogspot.nl/2009/01/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            mInstance = new ParseOpenHelper(ctx.getApplicationContext());
        }
        return mInstance;
    }

    public ParseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table alljobs(techid TEXT,jobid TEXT,jobsskeleton TEXT,jobstechdashboardnotes TEXT,jobstechlaborperform TEXT,jobstechofftherecord TEXT,jobstechuploadedimages TEXT,jobstechpartsrecord TEXT,jobstechlcrecord TEXT);");
        db.execSQL("create table alljobscurrentDay(techidcurrday TEXT,jobidcurrday TEXT,jobsskeletoncurrday TEXT,jobsskeletoncurrday_date TEXT,jobstechdashboardnotescurrday TEXT,jobstechlaborperformcurrday TEXT,jobstechofftherecordcurrday TEXT,jobstechuploadedimagescurrday TEXT,jobstechpartsrecordcurrday TEXT,jobstechlcrecordcurrday TEXT);");
        db.execSQL("create table tablemanufacturer(allmaufacturer TEXT);");
        db.execSQL("create table tablepickUpJobs(PICKUPtechid TEXT,PICKUPcustomername TEXT,PICKUPjobid TEXT,PICKUPcustomertypename TEXT,PICKUPregionName TEXT,PICKUPestimatedhr TEXT);");
        db.execSQL("create table tableneedestimate(estimatecreatedat TEXT,estimatetechid TEXT,estimatejobid TEXT,estimatedescription TEXT,estimateurgent TEXT);");
        db.execSQL("create table tablesubmitmylabornewoffrecord(SUBMITLABORtechid TEXT,SUBMITLABORjobid TEXT,SUBMITLABORtype TEXT,SUBMITLABORtime TEXT,SUBMITLABORnotes,SUBMITLABORrest TEXT);");
        db.execSQL("create table tablelcchange(LCCHANGEtechid TEXT,LCCHANGEjobid TEXT,LCCHANGElc TEXT,LCCHANGEstarttime TEXT,LCCHANGEendtime TEXT,LCCHANGEhours TEXT,LCCHANGEhoursAdjusted TEXT,LCCHANGEcreatedby TEXT,LCCHANGEcreatedAt TEXT,LCCHANGEcount TEXT);");
        db.execSQL("create table tablejobstatus(JOBSTATUStechid TEXT,JOBSTATUSjobid TEXT,JOBSTATUScompleted TEXT,JOBSTATUScaptionaware TEXT,JOBSTATUSnotes TEXT,JOBSTATUSsupplyAmount TEXT,JOBSTATUSnbillablehrs TEXT,JOBSTATUSnbillablehrsDesc TEXT,JOBSTATUSreturnimmid TEXT,JOBSTATUSalreadyScheduled TEXT,JOBSTATUSreason TEXT,JOBSTATUSdescription TEXT,JOBSTATUSstart_T TEXT,JOBSTATUSend_T TEXT,JOBSTATUShours TEXT,JOBSTATUShours_adjusted TEXT,JOBSTATUShours_adjustedend TEXT,JOBSTATUlabour_code TEXT,JOBSTATUCreatedAt TEXT,JOBSTATUBoatname TEXT,JOBSTATUHullid TEXT,JOBSTATUCaptionname TEXT,JOBSTATUScount TEXT);");
        db.execSQL("create table tableuploadimages(UPLOADIMAGEScreatedBy TEXT,UPLOADIMAGESjobid TEXT,UPLOADIMAGESDescr TEXT,UPLOADIMAGESCreatedAt TEXT,UPLOADIMAGESallimages TEXT);");

        db.execSQL("create table tableaddpart(ADDPARTcount TEXT,ADDPARTtechid TEXT,ADDPARTjobid TEXT,ADDPARTurgent TEXT,ADDPARTdescription TEXT,ADDPARThowFastNeeded TEXT,ADDPARTpriceapprovalRequired TEXT,ADDPARTforRepair TEXT,ADDPARTmanufacid TEXT,ADDPARTno TEXT,ADDPARTquantityneeded TEXT,ADDPARTserialNo TEXT,ADDPARTfailuredesc TEXT,ADDPARTtechSupportName TEXT,ADDPARTsetRmaorCase TEXT,ADDPARTmfgDeemThisWarranty TEXT,ADDPARTadvanceReplacement TEXT,ADDPARTSoldBySeatech TEXT,ADDPARTNeedLoaner TEXT,ADDPARTNotes TEXT,ADDPARTCreatedAt TEXT,ADDPARTUploadedIMages TEXT);");

        db.execSQL("create table tableurgentmsg(URGENTtechid TEXT,URGENTjobid TEXT,URGENT_custname TEXT,URGENT_custtype TEXT,URGENT_boatmkyear TEXT,URGENT_boatname TEXT,URGENT_Sender TEXT,URGENT_Receiver TEXT,URGENT_message TEXT,URGENT_createdat TEXT,URGENT_acknowledge TEXT,URGENT_messageid TEXT,URGENT_receiverid TEXT);");

        db.execSQL("create table tablenotifications(NOTItechid TEXT,NOTIrest TEXT);");
        db.execSQL("create table tablechatparentleft(CHATparent_teqid TEXT,CHATparent_jobid TEXT,CHATparent_cusname TEXT,CHATparent_custype TEXT," +
                "CHATparent_bmy TEXT,CHATparent_bname TEXT,CHATparent_newmsg TEXT);");
        db.execSQL("create table tablechatmsgs(CHAT_teqid TEXT,CHAT_jobid TEXT,CHAT_rest TEXT);");
        db.execSQL("create table tableschedulefilter(regionData TEXT,technicianData TEXT,jobsData TEXT,customerData TEXT);");
        db.execSQL("create table tablescheduledata(scheduledData TEXT);");
        db.execSQL("create table tablecalendardata(schedulecalendarData TEXT);");
        db.execSQL("create table tableweekdata(scheduleweekData TEXT);");
        db.execSQL("create table tabledaydata(scheduledayData TEXT);");
        db.execSQL("create table tablestartjob(isjobstarted TEXT,jobstartedjobid TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_ALLJOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_ALLJOBSCURRENTDAY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_MANUFACTURER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PICKUPJOBS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLENAME_NEEDESTIMATE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBMITMYLABOR_NEWOFFRECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LCCHANGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBSTATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_UPLOADIMAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ADDPART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_URGENTMSG);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATPARENTLEFT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATMSGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULEFILTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULEDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULECALENDARDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULEWEEKDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SCHEDULEDAYDATA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STARTJOB);
        onCreate(db);
    }
}