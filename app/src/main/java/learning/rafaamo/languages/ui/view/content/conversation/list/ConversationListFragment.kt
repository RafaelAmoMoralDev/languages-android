package learning.rafaamo.languages.ui.view.content.conversation.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import learning.rafaamo.languages.R
import learning.rafaamo.languages.data.datasource.entity.Conversation
import learning.rafaamo.languages.data.datasource.remote.util.*
import learning.rafaamo.languages.databinding.FragmentConversationListBinding
import learning.rafaamo.languages.ui.util.ItemDecorator
import learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet.ConversationBottomSheet

@AndroidEntryPoint
class ConversationListFragment: Fragment() {

  private val viewModel: ConversationListViewModel by viewModels()
  private lateinit var binding: FragmentConversationListBinding
  private var adapter: ConversationAdapter? = null

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    binding = FragmentConversationListBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
      viewModel.response.collect { response ->
        when (response) {
          is Resource.Load -> {
            binding.loader.show()
          }
          is Resource.Success -> {
            binding.tvMessage.isVisible = false
            onDataLoaded(response.data)
          }
          is Resource.Error -> {
            binding.loader.hide()
            binding.tvMessage.apply {
              text = getString(R.string.error_unknown)
              isVisible = true
            }
          }
        }
      }
    }
  }

  private fun onDataLoaded(list: List<Conversation>) {
    binding.loader.hide()

    if (list.isEmpty()) {
      binding.tvMessage.apply {
        text = getString(R.string.fragment_conversation_list_empty_no_conversations)
        isVisible = true
      }
    } else {
      setList(list)
    }
  }

  private fun setList(list: List<Conversation>) {
    // TODO: Comprobar si es correcto
    // De esta forma instanciamos el adaptador solamente una vez, la primera en la que obtengamos datos.
    if (this@ConversationListFragment.adapter == null) {
      this@ConversationListFragment.adapter = ConversationAdapter(
        onUserClicked = {
          binding.root.findNavController().navigate(
            ConversationListFragmentDirections.actionProfileToUserDetail(it.id, it.name)
          )
        },
        onEditClicked = {
          ConversationBottomSheet.newInstance(
            learning.rafaamo.languages.ui.view.content.conversation.bottom_sheet.Conversation(it.id, it.location, it.datetime)
          ).show(this@ConversationListFragment.childFragmentManager, ConversationBottomSheet.TAG)
        }
      )
      binding.list.apply {
        addItemDecoration(ItemDecorator(resources.getDimensionPixelSize(R.dimen.space_6)))
        adapter = this@ConversationListFragment.adapter
      }
    }

    binding.list.isVisible = true
    adapter!!.updateList(list)
  }

  override fun onDestroyView() {
    super.onDestroyView()
    // Igualamos el adaptador a null para que si volvemos a este fragmento sea el método setList el encargador de inicializar la lista
    // si es la primera vez que hemos obtenido algún dato.
    adapter = null
  }

}