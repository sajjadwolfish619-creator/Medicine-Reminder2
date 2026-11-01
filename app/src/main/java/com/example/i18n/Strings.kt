package com.example.i18n

object Strings {

    fun get(lang: AppLanguage, key: String): String {
        val faMap = mapOf(
            "app_name" to "Medicine Reminder",
            "greeting_morning" to "صبح بخیر",
            "greeting_afternoon" to "عصر بخیر",
            "greeting_evening" to "شب بخیر",
            "nav_home" to "خانه",
            "nav_today" to "امروز",
            "nav_medicines" to "داروها",
            "nav_history" to "تاریخچه",
            "nav_settings" to "تنظیمات",
            "stats_medicines" to "داروهای فعال",
            "stats_today_doses" to "نوبتهای امروز",
            "stats_taken" to "مصرفشده",
            "stats_remaining" to "باقیمانده",
            "stats_missed" to "مصرفنشده",
            "today_progress" to "پیشرفت مصرف امروز",
            "next_reminder" to "نزدیکترین یادآور",
            "no_upcoming_reminders" to "برای امروز نوبت دیگری باقی نمانده است",
            "add_medicine" to "افزودن داروی جدید",
            "edit_medicine" to "ویرایش دارو",
            "delete_medicine" to "حذف دارو",
            "medicine_name" to "نام دارو",
            "medicine_name_hint" to "مثلاً استامینوفن، آتورواستاتین...",
            "dosage" to "مقدار مصرف",
            "dosage_hint" to "مثلاً ۱ عدد، ۵۰۰ میلی‌گرم، ۵ سی‌سی",
            "form" to "شکل دارو",
            "form_tablet" to "قرص",
            "form_capsule" to "کپسول",
            "form_syrup" to "شربت",
            "form_drops" to "قطره",
            "form_injection" to "آمپول",
            "form_inhaler" to "اسپری",
            "form_ointment" to "پماد",
            "form_other" to "سایر",
            "instructions" to "توضیحات و دستور مصرف",
            "instructions_hint" to "مثلاً بعد از غذا، همراه با یک لیوان آب",
            "start_date" to "تاریخ شروع",
            "end_date" to "تاریخ پایان (اختیاری)",
            "ongoing" to "ادامه‌دار (بدون پایان)",
            "dose_times" to "ساعتهای مصرف در شبانه‌روز",
            "add_time" to "افزودن ساعت مصرف",
            "save" to "ذخیره",
            "cancel" to "انصراف",
            "confirm" to "تأیید",
            "delete" to "حذف",
            "search_medicine" to "جستوجوی نام دارو...",
            "catalog_suggestions" to "فهرست داروهای پیشنهادی",
            "catalog_disclaimer" to "فهرست بالا صرفاً جهت راهنمایی نام و شکل دارو است. مقدار و زمان مصرف باید طبق دستور پزشک وارد شود.",
            "status_taken" to "مصرف شد",
            "status_missed" to "مصرف نشد",
            "status_pending" to "در انتظار",
            "action_taken" to "ثبت مصرف",
            "action_missed" to "فراموش شد",
            "action_undo" to "بازگردانی",
            "no_medicines_yet" to "هنوز دارویی ثبت نشده است",
            "no_medicines_desc" to "برای شروع، با زدن دکمه زیر اولین داروی خود را ثبت کنید.",
            "no_doses_today" to "برای امروز نوبتی تنظیم نشده است",
            "sound_title" to "صدای هشدار",
            "sound_gentle" to "آرام و دلنشین (Gentle)",
            "sound_classic" to "کلاسیک (Classic)",
            "sound_digital" to "دیجیتال (Digital)",
            "sound_loud" to "بلند و قوی (Loud)",
            "sound_soft_bell" to "ناقوس ملایم (Soft Bell)",
            "preview_sound" to "پیشنمایش صدا",
            "stop_sound" to "توقف پخش",
            "sound_selected" to "صدای پیشفرض تنظیم شد",
            "profiles_title" to "مدیریت پروفایلها",
            "active_profile" to "پروفایل فعال",
            "add_profile" to "افزودن پروفایل جدید",
            "profile_name" to "نام شخص",
            "profile_relation" to "نسبت",
            "relation_self" to "خودم",
            "relation_mother" to "مادر",
            "relation_father" to "پدر",
            "relation_child" to "فرزند",
            "relation_spouse" to "همسر",
            "relation_other" to "سایر",
            "history_title" to "تاریخچهٔ مصرف دارو",
            "adherence_rate" to "میزان پایبندی به برنامه",
            "filter_all" to "همه داروها",
            "no_history_records" to "هنوز سابقه‌ای ثبت نشده است",
            "backup_title" to "پشتیبانگیری و بازیابی",
            "export_backup" to "تهیه نسخه پشتیبان (JSON)",
            "import_backup" to "بازیابی نسخه پشتیبان",
            "backup_copied" to "اطلاعات پشتیبان کپی شد",
            "backup_success" to "بازیابی اطلاعات با موفقیت انجام شد",
            "backup_error" to "خطا در بازیابی فایل یا اطلاعات نامعتبر است",
            "settings_language" to "زبان برنامه",
            "settings_theme" to "حالت تاریک",
            "settings_notifications" to "اعلانهای یادآوری",
            "settings_vibration" to "لرزش همراه با اعلان",
            "test_alarm_notification" to "آزمایش زنگ و اعلان یادآوری",
            "test_alarm_sent" to "اعلان آزمایشی ارسال شد",
            "about_title" to "درباره برنامه",
            "about_app_desc" to "این برنامه توسط سجاد احمدی ساخته شده که استرس فراموشی مصرف دارو در زمان درست از بین بره و آسودگی خاطر برای سلامت پزشکی بوجود بیاد. این برنامه تلاش من و هوش مصنوعی بود که سخت ولی لذت بخش بود",
            "version" to "نسخهٔ ۱.۰.۰",
            "delete_confirm_msg" to "آیا از حذف این مورد اطمینان دارید؟ تمامی یادآورهای مربوطه حذف خواهند شد.",
            "alarm_rescheduled" to "تمامی یادآورها با موفقیت بهروزرسانی شدند."
        )

        val enMap = mapOf(
            "app_name" to "Medicine Reminder",
            "greeting_morning" to "Good Morning",
            "greeting_afternoon" to "Good Afternoon",
            "greeting_evening" to "Good Evening",
            "nav_home" to "Home",
            "nav_today" to "Today",
            "nav_medicines" to "Medicines",
            "nav_history" to "History",
            "nav_settings" to "Settings",
            "stats_medicines" to "Active Meds",
            "stats_today_doses" to "Today's Doses",
            "stats_taken" to "Taken",
            "stats_remaining" to "Remaining",
            "stats_missed" to "Missed",
            "today_progress" to "Today's Adherence",
            "next_reminder" to "Next Reminder",
            "no_upcoming_reminders" to "No more doses scheduled for today",
            "add_medicine" to "Add Medicine",
            "edit_medicine" to "Edit Medicine",
            "delete_medicine" to "Delete Medicine",
            "medicine_name" to "Medicine Name",
            "medicine_name_hint" to "e.g., Acetaminophen, Atorvastatin...",
            "dosage" to "Prescribed Dosage",
            "dosage_hint" to "e.g., 1 tablet, 500 mg, 5 ml",
            "form" to "Medicine Form",
            "form_tablet" to "Tablet",
            "form_capsule" to "Capsule",
            "form_syrup" to "Syrup",
            "form_drops" to "Drops",
            "form_injection" to "Injection",
            "form_inhaler" to "Inhaler",
            "form_ointment" to "Ointment",
            "form_other" to "Other",
            "instructions" to "Instructions & Notes",
            "instructions_hint" to "e.g., After food, with plenty of water",
            "start_date" to "Start Date",
            "end_date" to "End Date (Optional)",
            "ongoing" to "Ongoing (No end date)",
            "dose_times" to "Daily Intake Times",
            "add_time" to "Add Intake Time",
            "save" to "Save",
            "cancel" to "Cancel",
            "confirm" to "Confirm",
            "delete" to "Delete",
            "search_medicine" to "Search medicine name...",
            "catalog_suggestions" to "Medication Directory",
            "catalog_disclaimer" to "Directory is for identifying medication names and forms only. Dosage and timing must follow your doctor's prescription.",
            "status_taken" to "Taken",
            "status_missed" to "Missed",
            "status_pending" to "Pending",
            "action_taken" to "Mark Taken",
            "action_missed" to "Mark Missed",
            "action_undo" to "Undo",
            "no_medicines_yet" to "No medicines registered yet",
            "no_medicines_desc" to "Tap the button below to add your first prescribed medication.",
            "no_doses_today" to "No doses scheduled for today",
            "sound_title" to "Alarm Sound",
            "sound_gentle" to "Gentle Chime",
            "sound_classic" to "Classic Alarm",
            "sound_digital" to "Digital Beep",
            "sound_loud" to "Loud Alert",
            "sound_soft_bell" to "Soft Bell",
            "preview_sound" to "Preview Sound",
            "stop_sound" to "Stop Preview",
            "sound_selected" to "Default sound updated",
            "profiles_title" to "User Profiles",
            "active_profile" to "Active Profile",
            "add_profile" to "Add Profile",
            "profile_name" to "Profile Name",
            "profile_relation" to "Relationship",
            "relation_self" to "Self",
            "relation_mother" to "Mother",
            "relation_father" to "Father",
            "relation_child" to "Child",
            "relation_spouse" to "Spouse",
            "relation_other" to "Other",
            "history_title" to "Medication History",
            "adherence_rate" to "Adherence Rate",
            "filter_all" to "All Medicines",
            "no_history_records" to "No intake history recorded yet",
            "backup_title" to "Backup & Restore",
            "export_backup" to "Export Backup (JSON)",
            "import_backup" to "Import Backup",
            "backup_copied" to "Backup data copied to clipboard",
            "backup_success" to "Backup restored successfully",
            "backup_error" to "Failed to restore backup: Invalid data format",
            "settings_language" to "App Language",
            "settings_theme" to "Dark Mode",
            "settings_notifications" to "Reminder Notifications",
            "settings_vibration" to "Vibration with Alarm",
            "test_alarm_notification" to "Test Alarm & Notification",
            "test_alarm_sent" to "Test reminder notification sent",
            "about_title" to "About Medicine Reminder",
            "about_app_desc" to "این برنامه توسط سجاد احمدی ساخته شده که استرس فراموشی مصرف دارو در زمان درست از بین بره و آسودگی خاطر برای سلامت پزشکی بوجود بیاد. این برنامه تلاش من و هوش مصنوعی بود که سخت ولی لذت بخش بود",
            "version" to "Version 1.0.0",
            "delete_confirm_msg" to "Are you sure you want to delete this? All associated alarms will be cancelled.",
            "alarm_rescheduled" to "All reminders were successfully updated."
        )

        return if (lang == AppLanguage.FA) {
            faMap[key] ?: key
        } else {
            enMap[key] ?: key
        }
    }

    fun formatNumber(num: Int, lang: AppLanguage): String {
        return if (lang == AppLanguage.FA) {
            num.toString().map { ch ->
                when (ch) {
                    '0' -> '۰'
                    '1' -> '۱'
                    '2' -> '۲'
                    '3' -> '۳'
                    '4' -> '۴'
                    '5' -> '۵'
                    '6' -> '۶'
                    '7' -> '۷'
                    '8' -> '۸'
                    '9' -> '۹'
                    else -> ch
                }
            }.joinToString("")
        } else {
            num.toString()
        }
    }

    fun formatTime(time: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.FA) {
            time.map { ch ->
                when (ch) {
                    '0' -> '۰'
                    '1' -> '۱'
                    '2' -> '۲'
                    '3' -> '۳'
                    '4' -> '۴'
                    '5' -> '۵'
                    '6' -> '۶'
                    '7' -> '۷'
                    '8' -> '۸'
                    '9' -> '۹'
                    else -> ch
                }
            }.joinToString("")
        } else {
            time
        }
    }
}
