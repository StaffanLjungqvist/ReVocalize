package se.staffanljungqvist.revocalize.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import se.staffanljungqvist.revocalize.PowerUp
import se.staffanljungqvist.revocalize.databinding.FragmentInventoryBinding
import se.staffanljungqvist.revocalize.viewmodels.IngameViewModel


class InventoryFragment : Fragment() {

    private val model: IngameViewModel by activityViewModels()

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        model.showInventory.observe(viewLifecycleOwner) {
            if (it) {
                binding.btnPowerSlizeRemove.isVisible = true
            } else if (!it) {
                binding.btnPowerSlizeRemove.isVisible = false
            }
        }

            binding.btnPowerSlizeRemove.setOnClickListener {
                model.usePowerUp(PowerUp.REMOVESLIZE)
                model.showInventory.value = false
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}