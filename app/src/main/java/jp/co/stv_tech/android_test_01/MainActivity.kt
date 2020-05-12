package jp.co.stv_tech.android_test_01

import android.graphics.Color
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.i("MainActivity", "onCreate")

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setLogo(R.mipmap.ic_launcher)
        setSupportActionBar(toolbar)
        val toolbarLayout = findViewById<CollapsingToolbarLayout>(R.id.toolbarLayout)
        toolbarLayout.title = getString(R.string.toolbar_title)
        toolbarLayout.setExpandedTitleColor(Color.WHITE)
        toolbarLayout.setCollapsedTitleTextColor(Color.LTGRAY)

        val lvShopList = findViewById<RecyclerView>(R.id.lvShopList)

        val layout = LinearLayoutManager(applicationContext)
        lvShopList.layoutManager = layout
        val shopList = createShopList()
        val adapter = RecyclerListAdapter(shopList)
        lvShopList.adapter = adapter


        val decorator = DividerItemDecoration(applicationContext, layout.orientation)
        lvShopList.addItemDecoration(decorator)



    }

    public override fun onStart() {
        Log.i("MainActivity", "onStart")



        //val shopName = item["name"] as String
        //val shopId = item["X086"] as String

        val receiver = ShopInfoReceiver()
        receiver.execute("X086")
        super.onStart()
    }
    private fun createShopList():MutableList<MutableMap<String, Any>> {
        Log.i("MainActivity", "createShopList")

        val shopList: MutableList<MutableMap<String, Any>> = mutableListOf()



        return shopList

    }
    private inner class RecyclerListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


        var tvgenreName: TextView
        var tvshopName: TextView
        var tvFood: TextView
        var tvAverage: TextView
        var tvOpen: TextView
        var tvAccess: TextView
        var tvAddress: TextView

        init {
            tvgenreName = itemView.findViewById(R.id.tvgenreName)
            tvshopName = itemView.findViewById(R.id.tvshopName)
            tvFood = itemView.findViewById(R.id.tvFood)
            tvAverage = itemView.findViewById(R.id.tvAverage)
            tvOpen = itemView.findViewById(R.id.tvOpen)
            tvAccess = itemView.findViewById(R.id.tvAccess)
            tvAddress = itemView.findViewById(R.id.tvAddress)
        }
    }
    private inner class RecyclerListAdapter(private val _listData: MutableList<MutableMap<String, Any>>):
        RecyclerView.Adapter<RecyclerListViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerListViewHolder {
            Log.i("MainActivity", "onCreateViewHolder")

            val inflater = LayoutInflater.from(applicationContext)

            val view = inflater.inflate(R.layout.row, parent, false)


            val holder = RecyclerListViewHolder(view)

            return holder
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            Log.i("MainActivity", "onBindViewHolder")

            val item = _listData[position]

            val genre = item["name"] as String
            val shopname = item["name"] as String
            val l = item["l"] as String
            val average = item["average"] as String
            val open = item["open"] as String
            val access = item["access"] as String
            val address = item["address"] as String

            holder.tvgenreName.text = genre
            holder.tvshopName.text = shopname
            holder.tvshopName.text = shopname
            holder.tvAverage.text = average
            holder.tvOpen.text = open
            holder.tvAccess.text = access
            holder.tvAddress.text = address



        }
        override fun getItemCount(): Int {
            Log.i("MainActivity", "getItemCount")

            return _listData.size
        }
    }



    private inner class  ShopInfoReceiver(): AsyncTask<String, String, String>() {
        override fun doInBackground(vararg params: String): String {
            Log.i("MainActivity", "doInBackground")

            val id = params[0]
            val urlStr = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=a0393a19b3f6514e&small_area=${id}&format=json"

            val url = URL(urlStr)
            val con = url.openConnection() as HttpURLConnection
            con.requestMethod = "GET"
            con.connect()
            val stream = con.inputStream
            val result = is2String(stream)
            con.disconnect()
            stream.close()

            return result
        }
        override fun onPostExecute(result: String) {

            Log.i("MainActivity", "onPostExecute")

            val rootJSON = JSONObject(result)
            val resultsJSON = rootJSON.getJSONObject("results")
            val shopJSON = resultsJSON.getJSONArray("shop")
            val shopObject = shopJSON.getJSONObject(0)

            val genre = shopObject.getJSONObject("genre")
            val genrename = genre.getString("name")

            val photo = shopObject.getJSONObject("photo")
            val mobile = photo.getJSONObject("mobile")
            val l = mobile.getString("l")

            //val food = shopObject.getJSONObject("food")
            //val foodname = food.getString("name")

            val shopname = shopObject.getString("name")

            val budget = shopObject.getJSONObject("budget")
            val average = budget.getString("average")

            val open = shopObject.getString("open")

            val access = shopObject.getString("access")

            val address = shopObject.getString("address")



        }
        private fun is2String(stream: InputStream): String {
            val sb = StringBuilder()
            val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
            var line = reader.readLine()
            while (line != null) {
                sb.append(line)
                line = reader.readLine()
            }
            reader.close()
            return sb.toString()
        }
    }


}
