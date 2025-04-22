package com.example.green_roots.fragments


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.green_roots.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class DirectionsFragment : Fragment() {
    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Configuración inicial de osmdroid
        Configuration.getInstance().userAgentValue = requireContext().packageName

        // Inflar el layout
        val view = inflater.inflate(R.layout.fragment_directions, container, false)
        mapView = view.findViewById(R.id.mapaView)
        mapView.setTileSource(TileSourceFactory.MAPNIK)

        // Configurar marcadores
        setupMapMarkers()

        return view
    }

    private fun setupMapMarkers() {
        // Marcador estático (Bogota)
        val bogota = GeoPoint(4.679240, -74.086958)
        val tienda1 = GeoPoint(4.681389, -74.089029)
        mapView.controller.setZoom(17.0)
        mapView.controller.setCenter(bogota)
        addMarker(bogota, "Ubcación", R.drawable.ic_location)
        addMarker(tienda1, "Verde pluma", R.drawable.ic_store)


    }

    private fun addMarker(point: GeoPoint, title: String, iconRes: Int) {
        val marker = Marker(mapView).apply {
            position = point
            this.title = title
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = ContextCompat.getDrawable(requireContext(), iconRes)
        }
        mapView.overlays.add(marker)
    }


    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}