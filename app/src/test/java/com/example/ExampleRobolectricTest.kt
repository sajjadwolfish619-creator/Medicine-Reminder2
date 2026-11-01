package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.backup.BackupManager
import com.example.data.db.AppDatabase
import com.example.data.model.Medicine
import com.example.data.model.UserProfile
import com.example.data.repository.MedicationCatalog
import com.example.data.repository.MedicineRepository
import com.example.i18n.AppLanguage
import com.example.i18n.Strings
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun readStringFromContext() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Medicine Reminder", appName)
    }

    @Test
    fun testMedicationCatalogSearch() {
        val matchesEn = MedicationCatalog.search("Acetaminophen")
        assertTrue("Should find Acetaminophen", matchesEn.isNotEmpty())

        val matchesFa = MedicationCatalog.search("استامینوفن")
        assertTrue("Should find استامینوفن", matchesFa.isNotEmpty())

        val matchesEmpty = MedicationCatalog.search("")
        assertTrue("Empty search should return top catalog items", matchesEmpty.isNotEmpty())
    }

    @Test
    fun testBilingualFormatting() {
        val faDigits = Strings.formatNumber(1234, AppLanguage.FA)
        assertEquals("۱۲۳۴", faDigits)

        val enDigits = Strings.formatNumber(1234, AppLanguage.EN)
        assertEquals("1234", enDigits)

        val timeFa = Strings.formatTime("08:30", AppLanguage.FA)
        assertEquals("۰۸:۳۰", timeFa)
    }

    @Test
    fun testRepositoryAndDatabaseOperations() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getInstance(context)
        val repo = MedicineRepository(db.medicineDao())

        // Ensure default profile
        val profile = repo.ensureDefaultProfile()
        assertNotNull(profile)
        assertTrue(profile.id > 0)

        // Save medicine with dose times
        val medId = repo.saveMedicineWithDoses(
            medicine = Medicine(
                profileId = profile.id,
                name = "Test Ibuprofen",
                dosage = "400 mg",
                form = "Tablet",
                startDate = "2026-01-01"
            ),
            times = listOf("08:00", "20:00")
        )
        assertTrue(medId > 0)

        val medWithDoses = repo.getMedicineWithDosesById(medId)
        assertNotNull(medWithDoses)
        assertEquals(2, medWithDoses!!.doseTimes.size)

        // Test marking dose
        repo.markDose(
            profileId = profile.id,
            medicine = medWithDoses.medicine,
            doseTime = medWithDoses.doseTimes.first(),
            dateStr = "2026-01-01",
            status = "TAKEN"
        )

        val todayDoses = repo.getTodayDoses(profile.id, "2026-01-01").first()
        assertTrue(todayDoses.isNotEmpty())
        assertEquals("TAKEN", todayDoses.first { it.doseTime.id == medWithDoses.doseTimes.first().id }.status)

        // Test Backup JSON generation
        val backupJson = BackupManager.createBackupJson(context)
        assertTrue(backupJson.contains("Test Ibuprofen"))
        assertTrue(backupJson.contains("TAKEN"))
    }
}
