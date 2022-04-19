package com.example.myapplication.util

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast

class Util {


    companion object{

        private const val TAG = "LOG_D_TAG"

        fun logD(str: String, tag: String = TAG){
            Log.d(tag, str)
        }

        fun toastShort(context: Context, str: String){
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
        }

        fun toastLong(context: Context, str: String){
            Toast.makeText(context, str, Toast.LENGTH_LONG).show()
        }

        /**
         * 遍历每行的每个的字符，并判断此字符是否可以作为单个CODE进行转换，如果可以则记录，
         * 遍历下一个字符并将其与上一个保留可以作为单个CODE的字符进行合并，并再次判断此合并后的字符是否可以进行转换，
         * 如果可以，则进行转换，如果不可以，则只使用上一个保留可以作为单个CODE的字符进行转换
         * 注意，只能一字符或二字符转换为一字符，若转换字符在deCodeMap中不存在则不进行转换并且不输出
         * @param lines 需要转换的字符串集合
         * @param deCodeMap 转换依据
         * @return 转换后的字符串
         */
        fun iniDeCode(lines: List<String>, deCodeMap: Map<String,String>): String{
            var deCodeString = ""
            for (line in lines){
                var resultIndex = line.indexOf('=')
                var txtVal = ""
                var txtDeCode = ""
                var txtFlg = ""
                var boolFlgY = false
                var isDoubleEnCode = false
                if(resultIndex != -1){
                    txtVal = line.substring(resultIndex+1)
                    for ((index,char) in txtVal.withIndex()){
                        if(isDoubleEnCode){
                            isDoubleEnCode = false
                            continue
                        }
                        if(!boolFlgY){
                            if(deCodeMap[char.toString()] != null){
                                boolFlgY = true
                                txtFlg += char
                                continue
                            }else{
                                var mapRes = deCodeMap[txtVal.substring(index,index+2)]
                                if(mapRes != null){
                                    isDoubleEnCode = true
                                    txtDeCode += mapRes
                                }else{
                                    if (!isDoubleEnCode){
                                        Log.d(TAG,"DeCode: $txtFlg / $char/$index  /  $line not found!!!-")
                                    }
                                }
                            }
                        }else{
                            var mapRes = deCodeMap[(txtFlg+char)]
                            if(mapRes != null){
                                if(!boolFlgY){
                                    isDoubleEnCode = true
                                }
                                txtDeCode += mapRes!!
                            }else{
                                txtDeCode += deCodeMap[(txtFlg)]!!
                            }
                            boolFlgY = false
                            txtFlg = ""
                        }
                    }
                    deCodeString += txtDeCode+"\n"
                    logD(txtDeCode)
                }else{
                    logD(line)
                }
            }
            return deCodeString
        }

        /**
         * 遍历每行的每个的字符，并判断此字符是否可以作为单个CODE进行转换，如果可以则记录，
         * 遍历下一个字符并将其与上一个保留可以作为单个CODE的字符进行合并，并再次判断此合并后的字符是否可以进行转换，
         * 如果可以，则进行转换，如果不可以，则只使用上一个保留可以作为单个CODE的字符进行转换
         * 注意，只能一字符或二字符转换为一字符，若转换字符在deCodeMap中不存在则不处理字符直接输出
         * @param lines 需要转换的字符串集合
         * @param deCodeMap 转换依据
         * @return 转换后的字符串
         */
        fun iniDeCodeAll(lines: List<String>, deCodeMap: Map<String,String>): String{
            var deCodeString = ""
            for (line in lines){
                var txtDeCode = ""
                var txtFlg = ""
                var boolFlgY = false
                var isDoubleEnCode = false
                for ((index,char) in line.withIndex()){
                    if(isDoubleEnCode){
                        isDoubleEnCode = false
                        continue
                    }
                    if(!boolFlgY){
                        if(deCodeMap[char.toString()] != null){
                            boolFlgY = true
                            txtFlg += char
                            continue
                        }else{
                            var mapRes: String? = ""
                            if(index > line.length-2){
                                mapRes = deCodeMap[char.toString()]
                            }else{
                                mapRes = deCodeMap[line.substring(index,index+2)]
                            }
                            if(mapRes != null){
                                isDoubleEnCode = true
                                txtDeCode += mapRes
                            }else{
                                txtDeCode += char
                                Log.d(TAG,"DeCode: $txtFlg / $char/$index  /  $line not found!!!-")
                            }
                        }
                    }else{
                        var mapRes = deCodeMap[(txtFlg+char)]
                        if(mapRes != null){
                            if(!boolFlgY){
                                isDoubleEnCode = true
                            }
                            txtDeCode += mapRes!!
                        }else{
                            txtDeCode += deCodeMap[txtFlg]!!
                        }
                        boolFlgY = false
                        txtFlg = ""
                    }
                }
                deCodeString += txtDeCode+"\n"
                logD(txtDeCode)
            }
            return deCodeString
        }

        fun iniDeCodePro(){

        }

        /**
         * 遍历每行的每个的字符，并判断此字符是否可以进行转换，是则转换，否则记录
         * @param lines 需要转换的字符串集合
         * @param enCodeMap 转换依据
         * @return 转换后的字符串
         */
        fun iniEnCode(lines: List<String>, enCodeMap: Map<String, String>): String{
            var enCodeString = ""
            for (line in lines){
                var resultIndex = line.indexOf('=')
                var txtVal = ""
                var txtEeCode = "+CVars="
                if(resultIndex != -1){
                    txtVal = line.substring(resultIndex+1)
                    for (char in txtVal){
                        var enCode = enCodeMap[char.toString()]
                            if(enCode != null){
                                txtEeCode += enCode
                            }else{
                                logD("iniEnCode EnCodeMap found: $char !!!")
                            }
                    }
                    enCodeString += txtEeCode+"\n"
                    logD(txtEeCode)
                }else{
                    enCodeString += line+"\n"
                }
            }
            return enCodeString
        }


        // 将Path转换为Android/Data/... Uri
        fun toAndroidDataUrl(path: String): Uri {
            var paths: List<String> = path.replace("/storage/emulated/0/Android/data", "").split("/")
            var stringBuilder = StringBuilder("content://com.android.externalstorage.documents/tree/primary%3AAndroid%2Fdata/document/primary%3AAndroid%2Fdata")
            for (path in paths) {
                stringBuilder.append("%2F").append(path)
            }
            return Uri.parse(stringBuilder.toString())
        }

    }

}