package com.example.myapplication

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.documentfile.provider.DocumentFile
import com.example.myapplication.util.Util
import java.io.*

class UCDeCodeEditActivity : AppCompatActivity(), View.OnClickListener{

    // 需要访问的Android/data目录下的具体文件
    private val pubgmdUCiniFilePath = "/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android/UserCustom.ini"
    // 需要访问的Android/data目录下的具体目录
    private val pubgmdUCiniFilesPath = "/storage/emulated/0/Android/data/com.tencent.tmgp.pubgmhd/files/UE4Game/ShadowTrackerExtra/ShadowTrackerExtra/Saved/Config/Android"
    // 完整的访问Uri
    private val pubgmdUCiniUri: Uri = Util.toAndroidDataUrl(pubgmdUCiniFilePath)

    private val pubgmdUCiniDeMap = mapOf("18" to "a","1B" to "b","1A" to "c","1D" to "d","1C" to "e","1F" to "f","1E" to "g","11" to "h",
        "10" to "i","13" to "j","12" to "k","15" to "l","14" to "m","17" to "n","16" to "o","09" to "p","08" to "q",
        "0B" to "r","0A" to "s","0D" to "t","0C" to "u","0F" to "v","0E" to "w","01" to "x","0" to "y","03" to "z","57" to ".","44" to "=",
        "38" to "A","3B" to "B","3A" to "C","3D" to "D","3C" to "E","3F" to "F","3E" to "G","31" to "H","30" to "I","33" to "J","32" to "K","35" to "L","34" to "M",
        "37" to "N","36" to "O","29" to "P","28" to "Q","2B" to "R","2A" to "S","1D" to "T","2C" to "U","2F" to "V","2E" to "W","21" to "X","20" to "Y","23" to "Z",
        "49" to "0","48" to "1","4B" to "2","4A" to "3","4D" to "4","4C" to "5","4F" to "6","4E" to "7","41" to "8","40" to "9")

    private val pubgmdUCiniEnMap = mapOf("a" to "18","b" to "1B","c" to "1A","d" to "1D","e" to "1C","f" to "1F","g" to "1E","h" to "11",
        "i" to "10","j" to "13","k" to "12","l" to "15","m" to "14","n" to "17","o" to "16","p" to "09","q" to "08",
        "r" to "0B","s" to "0A","t" to "0D","u" to "0C","v" to "0F","w" to "0E","x" to "01","y" to "0","z" to "03","." to "57","=" to "44",
        "A" to "38","B" to "3B","C" to "3A","D" to "3D","E" to "3C","F" to "3F","G" to "3E","H" to "31","I" to "30","J" to "33","K" to "32","L" to "35","M" to "34",
        "N" to "37","O" to "36","P" to "29","Q" to "28","R" to "2B","S" to "2A","T" to "1D","U" to "2C","V" to "2F","W" to "2E","X" to "21","Y" to "20","Z" to "23",
        "0" to "49","1" to "48","2" to "4B","3" to "4A","4" to "4D","5" to "4C","6" to "4F","7" to "4E","8" to "41","9" to "40")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ucedit)
        findViewById<Button>(R.id.btnDeCodeSave).setOnClickListener(this)
        readUCText()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnDeCodeSave -> {
                var ucTextLines = findViewById<EditText>(R.id.edtmlUCDeCode).text.toString().split("\n")
//                var ucTextLines = findViewById<EditText>(R.id.edtmlUC).text.toString()
                var iniEnCodeText = Util.iniEnCode(ucTextLines, pubgmdUCiniEnMap)

                val documentFile: DocumentFile = DocumentFile.fromSingleUri(this, pubgmdUCiniUri)!!
                Util.logD(documentFile.type.toString())
                val documentFileParent: DocumentFile = DocumentFile.fromTreeUri(this, Util.toAndroidDataUrl(pubgmdUCiniFilesPath))!!
                documentFile.delete()
                val ucFile = documentFileParent.createFile("application/octet-stream","UserCustom.ini")
                var output: OutputStream
                var writer: BufferedWriter? = null
                try {
                    output = contentResolver.openOutputStream(ucFile!!.uri)!!
                    writer = BufferedWriter(OutputStreamWriter(output))
                    writer.write(iniEnCodeText)
                    writer.flush()
                    writer?.close()
                }catch (e: IOException){
                    writer?.close()
                }

            }
        }
    }

    private fun readUCText(){
            val documentFile: DocumentFile = DocumentFile.fromSingleUri(this, pubgmdUCiniUri)!!
        Util.logD("pubgmdUCiniUri: $pubgmdUCiniUri \n file name: ${documentFile.name}\n" +
                " file path: ${documentFile.uri.path} \n" +
                " file encodedPath: ${documentFile.uri.encodedPath}")
        var input: InputStream
        var reader: BufferedReader? = null
        var lines: List<String>? = null
        try {
            input= contentResolver.openInputStream(documentFile.uri)!!
            reader = BufferedReader(InputStreamReader(input))
            lines = reader.readLines()
            reader?.close()
        }catch (e: IOException){
            reader?.close()
            e.printStackTrace()
        }
//            val output: OutputStream = contentResolver.openOutputStream(documentFile.uri)!!
//            val writer: BufferedWriter = BufferedWriter(OutputStreamWriter(output))
        if(lines?.size!! > 0){
            var ucStr = Util.iniDeCodeAll(lines,pubgmdUCiniDeMap)
            findViewById<EditText>(R.id.edtmlUCDeCode).setText(ucStr)
            Util.logD(ucStr)
        }else{
            Util.logD("ini lines size < 0 !!!")
        }
    }



}