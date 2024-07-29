package bdl.lockey.ghtk_permissions

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import bdl.lockey.ghtk_permissions.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnGetLocation.setOnClickListener {
            checkPermission(ACCESS_FINE_LOCATION, LOCATION_PERMISSION_CODE)
        }

    }

    private fun checkPermission(permission: String, requestCode: Int) {
        when {
            // Xay ra khi quyen truy cap da duoc bat san
            ContextCompat.checkSelfPermission(this, permission)
                    == PackageManager.PERMISSION_GRANTED
            -> {
                binding.tvPermissionStatus.text = "Trạng thái cấp quyền: Được cho phép"
                Snackbar.make(
                    binding.root,
                    R.string.location_permission_available,
                    Snackbar.LENGTH_SHORT
                ).show()
                getLocation()
            }

            // Xay ra khi truoc do duoc yeu cau cap quyen nhung bi tu choi
            // va khong chon "Khong bao gio hoi lai"
            // Tu choi cap quyen do shouldShowRequest... goi len
            // thi sau nay showShouldRequest... luon tra ve false = "Khong bao gio hoi lai"
            // va se luon chay vao nhanh else de tu choi cap quyen
            ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            -> {
                // Dua ra ly do de bat quyen truy cap
                Snackbar.make(
                    binding.root, R.string.location_access_required,
                    Snackbar.LENGTH_INDEFINITE
                ).setAction(R.string.ok, View.OnClickListener { // Request the permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(ACCESS_FINE_LOCATION),
                        LOCATION_PERMISSION_CODE
                    )
                }).show()
            }
            // Xay ra chi khi lan dau xin cap quyen
            // Khi khong chay check shouldShowRequest... do tu choi cap quyen va khong hoi lai,
            // se chay vao else nhung se khong hien thi yeu cau cap quyen
            // va tu choi luon viec cap quyen
            else -> {
                Snackbar.make(binding.root, R.string.location_unavailable, Snackbar.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(permission),
                    requestCode
                )
            }

        }
    }

    // ACCESS_FINE_LOCATION
    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    if (location != null) {
                        binding.tvLatitude.text = "Vĩ độ: ${location.latitude}"
                        binding.tvLongitude.text = "Kinh độ: ${location.longitude}"
                    } else {
                        Toast.makeText(this, "Không thể lấy vị trí", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                binding.tvPermissionStatus.text = "Trạng thái cấp quyền: Được cho phép"
                binding.btnGetLocation.isEnabled = true

                // Thong bao quyen duoc cap
                Snackbar.make(
                    binding.root, R.string.location_permission_granted,
                    Snackbar.LENGTH_SHORT
                )
                    .show()
                getLocation()
            } else { // Xay ra khi xin cap quyen bi tu choi
                binding.tvPermissionStatus.text = "Trạng thái cấp quyền: Bị từ chối"
                binding.btnGetLocation.isEnabled = false

                // Thong bao quyen bi tu choi
                Snackbar.make(
                    binding.root, R.string.location_permission_denied,
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        } else {
            // Ignore all other requests.
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}