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

class UCEnCodeEditActivity : AppCompatActivity(), View.OnClickListener{

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

    private val backUpIni = "[BackUp DeviceProfile]\n" +
            "+CVars=0B5734161B10151C3A16170D1C170D2A1A18151C3F181A0D160B44485749\n" +
            "+CVars=0B572A0D180D101A341C0A1135363D3D100A0D18171A1C2A1A18151C44495741\n" +
            "+CVars=1F161510181E1C5735363D3D100A0D18171A1C2A1A18151C4448\n" +
            "+CVars=0B5729180B0D101A151C35363D3B10180A4449\n" +
            "+CVars=0B573C14100D0D1C0B2A09180E172B180D1C2A1A18151C4448\n" +
            "+CVars=0B573D1C0D18101534161D1C444B\n" +
            "+CVars=0B572A11181D160E280C1815100D004448\n" +
            "+CVars=0B572A11181D160E573418013A2A342B1C0A16150C0D1016174448494B4D\n" +
            "+CVars=0B572A11181D160E573A2A345734180134161B10151C3A180A1A181D1C0A444B\n" +
            "+CVars=0B572A11181D160E573D100A0D18171A1C2A1A18151C4449574C\n" +
            "+CVars=0B573418013817100A160D0B160900444D\n" +
            "+CVars=0B5734180D1C0B101815280C1815100D00351C0F1C154448\n" +
            "+CVars=0B572C0A1C0B280C1815100D002A1C0D0D10171E444B\n" +
            "+CVars=0B5734161B10151C573D00171814101A361B131C1A0D2A11181D160E4448\n" +
            "+CVars=0B573D1C090D11361F3F101C151D280C1815100D004449\n" +
            "+CVars=0B572B1C1F0B181A0D101617280C1815100D004449\n" +
            "+CVars=1F161510181E1C5734101735363D4449\n" +
            "+CVars=0B572A0D0B1C181410171E57291616152A10031C444A4949\n" +
            "+CVars=0B5734161B10151C573F160B1A1C3C17181B151C303B354448\n" +
            "+CVars=0B57292C3B3E2F1C0B0A101617444C\n" +
            "+CVars=0B572C0A1C0B313D2B2A1C0D0D10171E4448\n" +
            "+CVars=0B5734161B10151C313D2B4449\n" +
            "+CVars=0B5734161B10151C572A1A1C171C3A1615160B3F160B14180D4449\n" +
            "+CVars=0B573B15161614280C1815100D004449\n" +
            "+CVars=0B5735101E110D2A11181F0D280C1815100D004449\n" +
            "+CVars=0B5734161B10151C572D16171C141809091C0B3F1015144449\n" +
            "+CVars=0B5734161B10151C5738150E18000A2B1C0A16150F1C3D1C090D114449\n" +
            "+CVars=0B572C0A1C0B342A38382A1C0D0D10171E4449\n" +
            "+CVars=0B5734161B10151C342A38384448\n" +
            "+CVars=0B573D1C1F180C150D3F1C180D0C0B1C5738170D10381510180A10171E4449\n" +
            "+CVars=0B5734161B10151C2A101409151C2A11181D1C0B4449\n" +
            "+CVars=0B5734161B10151C370C143D00171814101A291610170D35101E110D0A4449\n" +
            "+CVars=0B572C0A1C0B2F0C151218172A1C0D0D10171E4449\n" +
            "+CVars=0B572C0A1C0B2D1C1814280C1815100D003C171118171A1C2A1C0D0D10171E4449\n" +
            "+CVars=0B5734161B10151C573C17181B151C303B354449"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_ucactivity)
        findViewById<Button>(R.id.btnEeCodeSave).setOnClickListener(this)
        readUCText()
    }

    override fun onClick(p0: View?) {
        when(p0?.id){
            R.id.btnEeCodeSave -> {
                var ucTextLines = findViewById<EditText>(R.id.edtmlUCEnCode).text.toString()

                val documentFile: DocumentFile = DocumentFile.fromSingleUri(this, pubgmdUCiniUri)!!
                Util.logD(documentFile.type.toString())
                val documentFileParent: DocumentFile = DocumentFile.fromTreeUri(this, Util.toAndroidDataUrl(pubgmdUCiniFilesPath))!!
                documentFile.delete()
                val ucFile = documentFileParent.createFile("application/octet-stream","UserCustom.ini")
                var output: OutputStream
                var writer: BufferedWriter? = null
                var backUpFlg = false
                try {
                    if (ucTextLines.indexOf("[BackUp DeviceProfile]") <= -1){
                        backUpFlg = true
                    }
                    output = contentResolver.openOutputStream(ucFile!!.uri)!!
                    writer = BufferedWriter(OutputStreamWriter(output))
                    if (backUpFlg) {
                        writer.write(ucTextLines+"\n"+backUpIni)
                    }else{
                        writer.write(ucTextLines)
                    }
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

        findViewById<EditText>(R.id.edtmlUCEnCode).setText(lines.toString())

    }

}