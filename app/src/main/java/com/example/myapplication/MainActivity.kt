package com.example.myapplication

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.util.Util
import java.io.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    // Android/data Uri
    private val androidDataUri = "content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata"
    // 需要访问的Android/data目录下的具体文件
    private val pubgmdUCiniPath = "/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini"
    // 完整的访问Uri
    private val pubgmdUCiniUri: Uri = toAndroidDataUrl()

    private lateinit var launcherUCEdit: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btnUCEdit).setOnClickListener(this)
        if(isGrantAndroidData()) else requestAccessAndroidData()

        launcherUCEdit = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            contentResolver.takePersistableUriPermission(
                it?.data!!.data!!, Intent.FLAG_GRANT_READ_URI_PERMISSION
                        or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            Toast.makeText(this,"授权成功",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnUCEdit-> {
                val intent = Intent(this, UCEditActivity::class.java)
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
            Util.logD("isGrantAndroidData False")
        }
        return false
    }


    // 将Path转换为Uri
    private fun toAndroidDataUrl(): Uri{
        var paths: List<String> = pubgmdUCiniPath.replace("/storage/emulated/0/Android/data", "").split("/")
        var stringBuilder = StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata")
        for (s in paths) {
            stringBuilder.append("%2F").append(s)
        }
        return Uri.parse(stringBuilder.toString())
    }

}