package thisissadeghi.kmpilot

import android.app.Application
import org.koin.android.ext.koin.androidContext

/**
 * Created by Ali Sadeghi
 * on 18,Dec,2024
 */

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@MyApplication)
        }
    }
}
