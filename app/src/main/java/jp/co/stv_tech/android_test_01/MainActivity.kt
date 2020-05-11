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
    private fun createShopList():MutableList<MutableMap<String, Any>> {
        Log.i("MainActivity", "createShopList")

        val shopList: MutableList<MutableMap<String, Any>> = mutableListOf()

        var shop = mutableMapOf<String, Any>("name" to "唐揚げ定食", "price" to 800,"desc" to "若鳥の唐揚げにサラダ、ご飯とお味噌汁が付きます。")
        shopList.add(shop)

        shop = mutableMapOf<String, Any>("name" to "ハンバーグ定食", "price" to 800,"desc" to "手ごねハンバーグにサラダ、ご飯とお味噌汁が付きます。")
        shopList.add(shop)

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
        private inner class ListItemClickListener : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Log.i("MainActivity", "onItemClick")

                val item = parent?.getItemAtPosition(position) as Map<String, String>
                val shopName = item["name"]
                val shopId = item["id"]

                val tvShopName = findViewById<TextView>(R.id.tvgenreName)

                val receiver = ShopInfoReceiver()
                receiver.execute(shopId)
            }
        }

        override fun onBindViewHolder(holder: RecyclerListViewHolder, position: Int) {
            Log.i("MainActivity", "onBindViewHolder")

            val item = _listData[position]
            val shopName = item["name"] as String
            val shopPrice = item["price"] as Int

            val shopPriceStr = shopPrice.toString()

            holder.tvshopName.text = shopName

            holder.tvAverage.text = shopPriceStr

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
            val urlStr = "https://webservice.recruit.co.jp/hotpepper/gourmet/v1/?key=sample&large_area=Z011"

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
            val shopJSON = rootJSON.getJSONObject("shop")
            val genre = shopJSON.getJSONObject("genre")
            val genrename = genre.getString("name")

            val subgenre = shopJSON.getJSONObject("subgenre")
            val subgenrename = subgenre.getString("name")

            val photo = shopJSON.getJSONObject("photo")
            val mobile = photo.getJSONObject("mobile")
            val i = mobile.getString("i")

            val food = shopJSON.getJSONObject("food")
            val foodname = food.getString("name")

            val shop = rootJSON.getJSONArray("shop")
            val shopNow = shop.getJSONObject(0)
            val shopname = shopNow.getString("name")

            val budget = shopJSON.getJSONObject("budget")
            val average = budget.getString("average")

            val open = shopJSON.getString("open")

            val access = shopJSON.getString("access")

            val address = shopJSON.getString("address")



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
