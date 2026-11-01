package com.example.data.repository

import com.example.data.model.MedicationItem

object MedicationCatalog {

    val items: List<MedicationItem> = listOf(
        MedicationItem("Acetaminophen", "استامینوفن", "Acetaminophen / Paracetamol", "Tablet", "500 mg", "Pain & Fever", "Take with a full glass of water", "با یک لیوان کامل آب مصرف شود"),
        MedicationItem("Gelofen", "ژلوفن", "Ibuprofen Softgel", "Capsule", "400 mg", "Pain & Inflammation", "Take after food to protect stomach", "بعد از غذا جهت محافظت معده مصرف شود"),
        MedicationItem("Ibuprofen", "ایبوپروفن", "Ibuprofen", "Tablet", "400 mg", "Pain & Inflammation", "Take with meals or milk", "همراه غذا یا شیر مصرف شود"),
        MedicationItem("Amoxicillin", "آموکسی‌سیلین", "Amoxicillin", "Capsule", "500 mg", "Antibiotic", "Complete full prescribed course", "دوره کامل درمان طبق دستور تکمیل شود"),
        MedicationItem("Azithromycin", "آزیترومایسین", "Azithromycin", "Tablet", "250 mg", "Antibiotic", "Take 1 hour before or 2 hours after meals", "یک ساعت قبل یا ۲ ساعت بعد غذا"),
        MedicationItem("Cefixime", "سفیکسیم", "Cefixime", "Tablet", "400 mg", "Antibiotic", "Take at regular intervals daily", "در ساعات معین روزانه مصرف شود"),
        MedicationItem("Cephalexin", "سفالکسین", "Cephalexin", "Capsule", "500 mg", "Antibiotic", "Take with plenty of fluid", "همراه مایعات فراوان مصرف شود"),
        MedicationItem("Ciprofloxacin", "سیپروفلوکساسین", "Ciprofloxacin", "Tablet", "500 mg", "Antibiotic", "Avoid dairy products near dose time", "از مصرف لبنیات نزدیک به نوبت خودداری شود"),
        MedicationItem("Metformin", "متفورمین", "Metformin HCl", "Tablet", "500 mg", "Diabetes", "Take with or right after main meal", "همراه یا بلافاصله پس از وعده غذایی اصلی"),
        MedicationItem("Gliclazide", "گلیکلازید", "Gliclazide", "Tablet", "80 mg", "Diabetes", "Take with breakfast", "همراه صبحانه مصرف شود"),
        MedicationItem("Losartan", "لوزارتان", "Losartan Potassium", "Tablet", "25 mg", "Blood Pressure", "Can be taken with or without food", "همراه غذا یا بدون غذا در ساعت مشخص"),
        MedicationItem("Amlodipine", "آملودیپین", "Amlodipine Besylate", "Tablet", "5 mg", "Blood Pressure", "Take once daily in the morning", "یک بار در روز ترجیحاً صبح‌ها"),
        MedicationItem("Propranolol", "پروپرانولول", "Propranolol HCl", "Tablet", "20 mg", "Cardiovascular", "Take before meals on a consistent schedule", "قبل از غذا و با برنامه منظم"),
        MedicationItem("Metoprolol", "متوپرولول", "Metoprolol Tartrate", "Tablet", "50 mg", "Cardiovascular", "Take with or immediately after meals", "همراه یا بلافاصله بعد از غذا"),
        MedicationItem("Atorvastatin", "آتورواستاتین", "Atorvastatin Calcium", "Tablet", "20 mg", "Cholesterol", "Take at bedtime for optimal effect", "ترجیحاً شب‌ها قبل از خواب"),
        MedicationItem("Aspirin", "آسپرین", "Acetylsalicylic Acid (ASA)", "Tablet", "80 mg", "Blood Thinner", "Take with food and water", "بعد از غذا با یک لیوان آب"),
        MedicationItem("Clopidogrel", "کلوپیدوگرل", "Clopidogrel", "Tablet", "75 mg", "Blood Thinner", "Take once daily at the same time", "روزی یک بار در زمان ثابت"),
        MedicationItem("Omeprazole", "امپرازول", "Omeprazole", "Capsule", "20 mg", "Gastrointestinal", "Take 30 minutes before breakfast", "۳۰ دقیقه قبل از صبحانه ناشتا"),
        MedicationItem("Pantoprazole", "پنتوپرازول", "Pantoprazole", "Tablet", "40 mg", "Gastrointestinal", "Take in the morning before eating", "صبح ناشتا نیم ساعت قبل غذا"),
        MedicationItem("Famotidine", "فاموتیدین", "Famotidine", "Tablet", "40 mg", "Gastrointestinal", "Take at bedtime or before meals", "قبل از خواب یا قبل از وعده غذایی"),
        MedicationItem("Dimethicone", "دایمتیکون", "Activated Dimethicone", "Tablet", "40 mg", "Gastrointestinal", "Chew well after meals and at bedtime", "بعد از غذا به خوبی جویده شود"),
        MedicationItem("Clidinium C", "کلیدینیوم سی", "Clidinium + Chlordiazepoxide", "Tablet", "1 Tablet", "Gastrointestinal", "Take before meals and before bed", "قبل از وعده‌های غذایی و قبل خواب"),
        MedicationItem("Diphenhydramine", "دیفن‌هیدرامین", "Diphenhydramine HCl", "Syrup", "5 ml", "Cough & Allergy", "May cause drowsiness", "ممکن است خواب‌آلودگی ایجاد کند"),
        MedicationItem("Diphenhydramine Compound", "دیفن هیدرامین کامپاند", "Diphenhydramine + Ammonium", "Syrup", "5 ml", "Cough & Cold", "Shake well before use", "قبل از مصرف به خوبی تکان داده شود"),
        MedicationItem("Cetirizine", "ستیریزین", "Cetirizine HCl", "Tablet", "10 mg", "Allergy", "Take once daily in evening", "روزی یک بار ترجیحاً عصر یا شب"),
        MedicationItem("Loratadine", "لوراتادین", "Loratadine", "Tablet", "10 mg", "Allergy", "Take once daily", "روزی یک بار همراه یا بدون غذا"),
        MedicationItem("Fexofenadine", "فکسوفنادین", "Fexofenadine HCl", "Tablet", "120 mg", "Allergy", "Avoid fruit juices within 2 hours", "تا ۲ ساعت آب‌میوه مصرف نشود"),
        MedicationItem("Prednisolone", "پردنیزولون", "Prednisolone", "Tablet", "5 mg", "Anti-inflammatory", "Take with breakfast", "صبح‌ها همراه صبحانه مصرف شود"),
        MedicationItem("Dexamethasone", "دگزامتازون", "Dexamethasone", "Tablet", "0.5 mg", "Corticosteroid", "Take as directed by doctor with food", "همراه غذا و دقیقاً طبق نسخه پزشک"),
        MedicationItem("Levothyroxine", "لووتیروکسین", "Levothyroxine Sodium", "Tablet", "50 mcg", "Thyroid", "Take on empty stomach 30-60 min before breakfast", "صبح ناشتا ۳۰ تا ۶۰ دقیقه قبل صبحانه"),
        MedicationItem("Sertraline", "سرترالین", "Sertraline HCl", "Tablet", "50 mg", "Mental Health", "Take once daily in morning or evening", "روزی یک عدد صبح یا عصر با آب"),
        MedicationItem("Fluoxetine", "فلوکستین", "Fluoxetine HCl", "Capsule", "20 mg", "Mental Health", "Take in the morning with water", "صبح‌ها با یک لیوان آب"),
        MedicationItem("Escitalopram", "اس‌سیتالوپرام", "Escitalopram", "Tablet", "10 mg", "Mental Health", "Take consistently at the same time", "هر روز در ساعت معین مصرف شود"),
        MedicationItem("Alprazolam", "آلپرازولام", "Alprazolam", "Tablet", "0.5 mg", "Sedative", "Use strictly as prescribed", "دقیقاً مطابق تجویز پزشک مصرف شود"),
        MedicationItem("Clonazepam", "کلونازپام", "Clonazepam", "Tablet", "1 mg", "Neurology", "Take at bedtime as prescribed", "شب‌ها قبل از خواب طبق دستور پزشک"),
        MedicationItem("Gabapentin", "گاباپنتین", "Gabapentin", "Capsule", "300 mg", "Nerve Pain", "Take regularly at scheduled times", "در ساعات معین با آب مصرف شود"),
        MedicationItem("Pregabalin", "پره‌گابالین", "Pregabalin", "Capsule", "75 mg", "Nerve Pain", "Take with or without food", "همراه یا بدون غذا مصرف شود"),
        MedicationItem("Naproxen", "ناپروکسن", "Naproxen", "Tablet", "500 mg", "Pain & Joint", "Take with meals to avoid stomach upset", "همراه غذا یا شیر مصرف شود"),
        MedicationItem("Diclofenac", "دیکلوفناک", "Diclofenac Sodium", "Tablet", "50 mg", "Pain & Joint", "Swallow whole with plenty of water", "کامل بلعیده شود و جویده نشود"),
        MedicationItem("Baclofen", "باکلوفن", "Baclofen", "Tablet", "10 mg", "Muscle Relaxant", "Take with food or milk", "همراه غذا یا شیر مصرف شود"),
        MedicationItem("Methocarbamol", "متوکاربامول", "Methocarbamol", "Tablet", "500 mg", "Muscle Relaxant", "Take with water as prescribed", "با آب کافی مصرف شود"),
        MedicationItem("Salbutamol Inhaler", "اسپری سالبوتامول", "Salbutamol (Albuterol)", "Inhaler", "2 Puffs", "Respiratory", "Inhale deeply as directed during shortness of breath", "در زمان تنگی نفس یا طبق دستور"),
        MedicationItem("Seretide Inhaler", "اسپری سرتاید", "Salmeterol / Fluticasone", "Inhaler", "1 Puff", "Respiratory", "Rinse mouth with water after use", "بعد از مصرف دهان با آب شسته شود"),
        MedicationItem("Artificial Tears", "اشک مصنوعی", "Polyvinyl Alcohol / Hypromellose", "Drops", "1-2 Drops", "Ophthalmology", "Apply in affected eye as needed", "در چشم درگیر چکانده شود"),
        MedicationItem("Vitamin D3", "ویتامین د۳", "Cholecalciferol", "Capsule", "50,000 IU", "Supplement", "Take with main meal containing fats", "همراه با وعده غذایی چرب"),
        MedicationItem("Multivitamin", "مولتی‌ویتامین", "Multivitamins & Minerals", "Tablet", "1 Tablet", "Supplement", "Take with food in the morning", "صبح‌ها همراه یا بعد صبحانه"),
        MedicationItem("Folic Acid", "اسید فولیک", "Folic Acid", "Tablet", "1 mg", "Supplement", "Take once daily with water", "روزی یک عدد همراه آب"),
        MedicationItem("Iron Supplement", "قرص آهن", "Ferrous Sulfate", "Tablet", "50 mg", "Supplement", "Take on empty stomach or with Vitamin C", "با معده خالی یا همراه ویتامین سی"),
        MedicationItem("Calcium + D3", "کلسیم د۳", "Calcium Carbonate + Vit D3", "Tablet", "1 Tablet", "Supplement", "Take with evening meal", "همراه وعده غذایی شام"),
        MedicationItem("Neurobion", "نوروبیون", "Vitamins B1, B6, B12", "Injection", "1 Ampoule", "Vitamins", "Intramuscular injection by nurse/doctor", "تزریق عضلانی توسط کادر درمان"),
        MedicationItem("Hydrochlorothiazide", "هیدروکلروتیازید", "HCTZ", "Tablet", "25 mg", "Diuretic", "Take in morning to avoid night urination", "صبح مصرف شود تا مانع خواب شب نشود"),
        MedicationItem("Spironolactone", "اسپیرونولاکتون", "Spironolactone", "Tablet", "25 mg", "Diuretic", "Take with food in the morning", "همراه غذا در ساعات اولیه روز"),
        MedicationItem("Warfarin", "وارفارین", "Warfarin Sodium", "Tablet", "5 mg", "Blood Thinner", "Take at exact same time every evening", "هر شب دقیقاً در ساعت مشخص")
    )

    fun search(query: String): List<MedicationItem> {
        val q = query.trim().lowercase()
        if (q.isEmpty()) return items.take(15)
        return items.filter { item ->
            item.nameEn.lowercase().contains(q) ||
            item.nameFa.contains(q) ||
            item.genericName.lowercase().contains(q) ||
            item.category.lowercase().contains(q)
        }
    }
}
