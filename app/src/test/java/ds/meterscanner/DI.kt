package ds.meterscanner

import com.bumptech.glide.RequestManager
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.bind
import com.github.salomonbrys.kodein.conf.global
import com.github.salomonbrys.kodein.erased.instance
import com.nhaarman.mockito_kotlin.mock
import ds.meterscanner.auth.Authenticator
import ds.meterscanner.data.Prefs
import ds.meterscanner.data.ResourceProvider
import ds.meterscanner.db.FirebaseDb
import ds.meterscanner.net.NetLayer
import ds.meterscanner.scheduler.Scheduler

fun viewModelKodein() = with(Kodein.global) {
    addImport(mockModule)
}

private val mockModule = Kodein.Module {
    bind() from instance(mock<Prefs>())
    bind() from instance(mock<FirebaseDb>())
    bind() from instance(mock<NetLayer>())
    bind() from instance(mock<Authenticator>())
    bind() from instance(mock<Scheduler>())
    bind() from instance(mock<ResourceProvider>())
    bind() from instance(mock<RequestManager>())
}