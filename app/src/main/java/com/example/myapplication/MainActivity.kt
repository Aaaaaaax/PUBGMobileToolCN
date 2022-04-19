package com.example.myapplication

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.view.View
import android.widget.Button
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.myapplication.util.Util

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object{
        // Android/data Uri
        val androidDataUri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"
        // 需要访问的Android/data目录下的具体文件
        val pubgmdUCiniFilePath = "/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini"
        // 完整的访问Uri
        val pubgmdUCiniUri: Uri = Util.toAndroidDataUrl(pubgmdUCiniFilePath)
    }

    private lateinit var launcherUCEdit: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnUCDeCodeEdit).setOnClickListener(this)
        findViewById<Button>(R.id.btnUCEnCodeEdit).setOnClickListener(this)
        if(isGrantAndroidData()) else requestAccessAndroidData()

        launcherUCEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){

            if(it.resultCode == RESULT_OK){
                contentResolver.takePersistableUriPermission(
                    it?.data!!.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION
                            or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                Util.toastShort(this,"授权成功!")
            }else{
                Util.toastShort(this,"授权失败!")
            }
        }

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnUCDeCodeEdit-> {
                val intent = Intent(this, UCDeCodeEditActivity::class.java)
                startActivity(intent)
            }
            R.id.btnUCEnCodeEdit-> {
                val intent = Intent(this, UCEnCodeEditActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 调用Android原生文件管理请求用户授权访问Android/data/...文件夹权限 Android 11以上必须使用此方法才可以访问Android/data目录
    private fun requestAccessAndroidData() {
        try {
            Util.logD("uri : $pubgmdUCiniUri")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE)
            intent.flags = (Intent.FLAG_GRANT_READ_URI_PERMISSION
                    or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                    or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, DocumentFile.fromTreeUri(this,Uri.parse(androidDataUri))?.uri)
            launcherUCEdit.launch(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 判断是否授权AndroidData访问权限
    private fun isGrantAndroidData(): Boolean {
        for (persistedUriPermission in contentResolver.persistedUriPermissions) {
            if (persistedUriPermission.uri.toString() == androidDataUri) {
                Util.logD("isGrantAndroidData True")
                return true
            }
        }
        Util.logD("isGrantAndroidData False")
        return false
    }

}