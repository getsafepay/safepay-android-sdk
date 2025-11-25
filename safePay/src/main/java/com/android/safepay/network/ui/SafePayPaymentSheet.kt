package com.android.safepay.network.ui

import CurrencyToCountryUtil
import CurrencyUtil
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.safepay.R
import com.android.safepay.databinding.ActivitySafepayBinding
import com.android.safepay.network.error.DebugLogger
import com.android.safepay.network.error.PaymentError
import com.android.safepay.network.interfaces.ApiResultCallback
import com.android.safepay.network.interfaces.ItemClick
import com.android.safepay.network.model.address.FetchAddressResponse
import com.android.safepay.network.model.address.GetAddressResponse
import com.android.safepay.network.model.address.Option
import com.android.safepay.network.model.payment.AuthenticationSetup
import com.android.safepay.network.model.payment.Authorization
import com.android.safepay.network.model.payment.AuthorizationRequest
import com.android.safepay.network.model.payment.AuthorizationResponse
import com.android.safepay.network.model.payment.Billing
import com.android.safepay.network.model.payment.EnrollmentRequest
import com.android.safepay.network.model.payment.EnrollmentResponse
import com.android.safepay.network.model.payment.PayerAuthenticationRequest
import com.android.safepay.network.model.payment.PayerAuthenticationResponse
import com.android.safepay.network.model.payment.Payload
import com.android.safepay.network.model.payment.PaymentResult
import com.android.safepay.network.model.payment.UnEnrollmentCardAuthorizationRequest
import com.android.safepay.network.model.paymentmethod.PaymentData
import com.android.safepay.network.model.paymentmethod.PaymentMethodListResponse
import com.android.safepay.network.model.token.ShopperTokenGenerateRequest
import com.android.safepay.network.model.token.TokenGenerateRequest
import com.android.safepay.network.model.token.TokenResponse
import com.android.safepay.network.model.tracker.TrackerFetchResponse
import com.android.safepay.network.model.user.UserExistsResponse
import com.android.safepay.network.model.usershopper.ShopperUserRequest
import com.android.safepay.network.model.usershopper.ShopperUserResponse
import com.android.safepay.network.network.RetrofitInstance
import com.android.safepay.network.repository.SafePayRepository
import com.android.safepay.network.ui.adapter.SavedCardAdapter
import com.android.safepay.network.utils.CardType
import com.android.safepay.network.utils.EnrollmentStatus
import com.android.safepay.network.utils.NetworkUtil
import com.android.safepay.network.utils.NextActions
import com.android.safepay.network.utils.Payment
import com.android.safepay.network.utils.TrackerStatus
import com.android.safepay.network.utils.UiUtils
import com.android.safepay.network.utils.UiUtils.collapse
import com.android.safepay.network.utils.UiUtils.enablePayButton
import com.android.safepay.network.utils.UiUtils.expand
import com.android.safepay.network.utils.UiUtils.hideBlockerView
import com.android.safepay.network.utils.UiUtils.hideKeyboard
import com.android.safepay.network.utils.UiUtils.isValidEmail
import com.android.safepay.network.utils.UiUtils.showBlockerView
import com.android.safepay.network.utils.Validator
import com.android.safepay.network.viewmodel.SafePayViewModel
import com.android.safepay.network.viewmodel.SafePayViewModelFactory
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalEnvironment
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalRenderType
import com.cardinalcommerce.cardinalmobilesdk.enums.CardinalUiType
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalActionCode
import com.cardinalcommerce.cardinalmobilesdk.models.CardinalConfigurationParameters
import com.cardinalcommerce.cardinalmobilesdk.models.ValidateResponse
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalInitService
import com.cardinalcommerce.cardinalmobilesdk.services.CardinalValidateReceiver
import com.cardinalcommerce.shared.userinterfaces.UiCustomization
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import java.util.Calendar


class SafePayPaymentSheet(val configuration: SafePayConfiguration): ApiResultCallback,ItemClick
{

    //VIEWS
    private var binding: ActivitySafepayBinding?=null

    //API LAYERS
    private lateinit var viewModel: SafePayViewModel
    private lateinit var repository: SafePayRepository

    // Store the callback function as a class-level variable
    private var paymentResultCallback: ((PaymentResult) -> Unit)? = null

    private var email:String=""
    private var month:String=""
    private var year:String=""
    private var countryCode:String=""
    private var currencySymbol:String?=""
    private var state:String=""
    private var amount:String=""
    private lateinit var validator:Validator
    private lateinit var mActivity: AppCompatActivity
    private lateinit var bottomSheetDialog:BottomSheetDialog
    private lateinit var spinnerAdapter:ArrayAdapter<Any>
    private var stateOptionItems = listOf<Option>()


    private lateinit var cardinal:com.cardinalcommerce.cardinalmobilesdk.Cardinal

    private var isPhoneValid:Boolean=false
    private var isPasswordValid:Boolean=false
    private var isCVCValid:Boolean=false
    private var isExpiryValid:Boolean=false
    private var isCardNumberValid:Boolean=false
    private var isCityValid:Boolean=false
    private var isPostalCodeValid:Boolean=false
    private var isAddressValid:Boolean=false
    private var isFirstNameValid:Boolean=false
    private var isLastNameValid:Boolean=false

    private var isFirstNameFocused:Boolean=false
    private var isLastNameFocused:Boolean=false
    private var isCVCFocused:Boolean=false
    private var isCardFocused:Boolean=false

    private var isFetchTrackerSuccess = false

    private var requiredFieldsViews = mutableListOf<AppCompatEditText?>()
    private var requiredFields= listOf<String>()

    var typingDelayHandler: Handler? = null
    var passowrdTypingDelayHandler: Handler? = null
    var typingCardDelayHandler: Handler? = null

    var isProgrammaticChange = false

    private val DEBOUNCE_DELAY = 500L


    companion object
    {
        const val API_CALL_FETCH_TRACKER = "fetch_tracker"
        const val API_CALL_USER_EXIST = "user_exist"
        const val API_CALL_SHOPPER_TOKEN = "shopper_token"
        const val API_CALL_SHOPPER_LOGIN = "shopper_login"
        const val API_CALL_GENERATE_TOKEN = "generate_token"
        const val API_CALL_PAYER_AUTHENTICATION = "payer_authentication"
        const val API_CALL_PAYER_AUTHENTICATION_SAVED_CARD = "payer_authentication_saved_card"
        const val API_CALL_ENROLLMENT = "enrollment"
        const val API_CALL_AUTHORIZATION = "authorization"
        const val API_CALL_UN_ENROLL_CARD = "un_enroll_card"
        const val API_CALL_CAPTURE = "capture"
        const val API_CALL_CREATE_SAFEPAY_SHOPPER = "create_safepay_shopper"
        const val API_CALL_GET_ADDRESS = "get_address"
        const val API_CALL_GET_PAYMENT_METHOD_LIST = "get_payment_method"
        const val API_CALL_GET_PRE_FETCH_ADDRESS = ""
        var IS_GUEST_USER = false

    }

    // Create and display the bottom sheet with the provided layout
    fun present(activity: AppCompatActivity, callback: (PaymentResult) -> Unit) {

        bottomSheetDialog = BottomSheetDialog(activity)
        binding = ActivitySafepayBinding.inflate(LayoutInflater.from(activity))
        binding?.root?.let { bottomSheetDialog.setContentView(it) }

        bottomSheetDialog.setOnShowListener { dialog ->
            val bottomSheet = (dialog as BottomSheetDialog).findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.isDraggable = false // Disable dragging
                behavior.state = BottomSheetBehavior.STATE_EXPANDED // Ensure it's fully expanded
            }
        }

        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()

        RetrofitInstance.setBaseURL(configuration.sandBox)
        DebugLogger.d("Tracker: " + configuration.trackerToken)
        mActivity=activity
        paymentResultCallback=callback
        validator=Validator(activity)
        spinnerAdapter = ArrayAdapter(mActivity, R.layout.spinner_item, stateOptionItems.map { it.name })
        initCardinalSDK()

        repository = SafePayRepository(mActivity)
        val factory = SafePayViewModelFactory(repository)

        viewModel = ViewModelProvider(activity,factory)[SafePayViewModel::class.java]

        callAPI(API_CALL_FETCH_TRACKER)

        setOnClickListeners()

    }

    private fun setOnClickListeners()
    {
        bottomSheetDialog.setOnDismissListener {
            reset()
            hideBlockerView(binding?.overlay)
        }

        binding?.footer?.tvTerms?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://safepaydocs.netlify.app/legal/tos/paying-customer"))
            mActivity.startActivity(intent)
        }

        binding?.footer?.tvPrivacy?.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://safepaydocs.netlify.app/legal/tos/privacy/"))
            mActivity.startActivity(intent)
        }

        binding?.btnPay?.setOnClickListener {
            if(NetworkUtil.isInternetConnected(mActivity))
            {
                hideErrorMsg()
                hideKeyboard(it)
                showLoading()
                if(viewModel.isCheckedSavedCard)
                {
                    showBlockerView(binding?.overlay)
                    callAPI(API_CALL_CREATE_SAFEPAY_SHOPPER)
                }
                else
                {
                    if(IS_GUEST_USER)
                    {
                        showBlockerView(binding?.overlay)
                        callAPI(API_CALL_GENERATE_TOKEN)
                    }

                    else
                    {
                        if(viewModel.isPaymentMethodClicked)
                        {
                            showBlockerView(binding?.overlay)
                            viewModel.isCheckedSavedCard=true
                            callAPI(API_CALL_PAYER_AUTHENTICATION)
                        }
                        else
                        {
                            showBlockerView(binding?.overlay)
                            callAPI(API_CALL_PAYER_AUTHENTICATION_SAVED_CARD)
                        }

                    }
                }
            }
            else
                Toast.makeText(mActivity,"Please check Internet",Toast.LENGTH_SHORT).show()
        }

        binding?.btnLogin?.setOnClickListener {
            if(NetworkUtil.isInternetConnected(mActivity))
            {
                if(isPasswordValid)
                {
                    hideKeyboard(it)
                    binding?.btnLogin?.text=""
                    hideErrorMsg()
                    showPasswordLoading()
                    callAPI(API_CALL_SHOPPER_LOGIN)
                }
            }
            else
                Toast.makeText(mActivity,"Please check Internet",Toast.LENGTH_SHORT).show()

        }

        binding?.cancel?.setOnClickListener {
            when(viewModel.payment)
            {
                Payment.CANCEL->
                {
                    paymentResultCallback?.invoke(PaymentResult.Canceled)
                }
            }
           // if(!viewModel.isPaymentSuccess)
             //   paymentResultCallback?.invoke(PaymentResult.Canceled)
            hideErrorMsg()
            hideBlockerView(binding?.overlay)
            bottomSheetDialog.dismiss()
            reset()
        }

        binding?.layoutPaymentMethod?.layoutAddPayment?.setOnClickListener {
            hideErrorMsg()
            viewModel.isPaymentMethodClicked=true
            enablePayButton(false,binding?.btnPay,mActivity)
            binding?.tvHeadingCard?.text="Add a payment method"
            binding?.tvCardDetailCancel?.visibility=View.VISIBLE
            binding?.layoutSaveCard?.visibility=View.GONE
            binding?.layoutPaymentMethod?.root?.visibility=View.GONE
            binding?.cardLayout?.let { expand(it) }
        }

        binding?.tvCardDetailCancel?.setOnClickListener {
            hideErrorMsg()
            //clearCardFields()
            viewModel.isPaymentMethodClicked=false
            binding?.layoutSaveCard?.visibility=View.VISIBLE
            enablePayButton(true,binding?.btnPay,mActivity)
            binding?.tvHeadingCard?.text="Card Details"
            binding?.tvCardDetailCancel?.visibility=View.GONE
            binding?.layoutPaymentMethod?.root?.let { expand(it) }
            binding?.cardLayout?.visibility=View.GONE
        }

        binding?.cbSaveCard?.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                viewModel.isCheckedSavedCard=true
                validateAndUpdateButtonState()
                enablePayButton(false,binding?.btnPay,mActivity)
                binding?.guestPasswordFieldLayout?.root?.let { expand(it) }
            } else {
                viewModel.isCheckedSavedCard=false
                validateAndUpdateButtonState()
                enablePayButton(areAllRequiredFieldsFilled(), binding?.btnPay, mActivity)
                isProgrammaticChange=true
                binding?.guestPasswordFieldLayout?.etGuestPassword?.text?.clear()
                isProgrammaticChange=false
               // binding?.guestPasswordFieldLayout?.etGuestPassword?.background=ContextCompat.getDrawable(mActivity,R.drawable.bg_edittext)
                binding?.guestPasswordFieldLayout?.root?.let { collapse(it) }
            }
        }
        binding?.countryCode?.setOnCountryChangeListener {
            binding?.hintCountry?.visibility=View.GONE
            binding?.countryCode?.visibility=View.VISIBLE
            countryCode=binding?.countryCode?.selectedCountryNameCode.toString()
            callAPI(API_CALL_GET_ADDRESS)
        }
        binding?.countryLayout?.setOnClickListener {
           binding?.countryCode?.launchCountrySelectionDialog()
        }

        var isPasswordVisible = false
        binding?.icPasswordToggle?.setOnClickListener {
            if(isPasswordVisible)
            {
                isPasswordVisible=false
                binding?.icPasswordToggle?.setImageResource(R.drawable.ic_eye_open)
                UiUtils.togglePasswordVisibility(false,binding?.etPassword!!)
            }
            else
            {
                isPasswordVisible=true
                binding?.icPasswordToggle?.setImageResource(R.drawable.ic_pass_eye)
                UiUtils.togglePasswordVisibility(true,binding?.etPassword!!)
            }

        }

        var isGuestPasswordVisible = false
        binding?.guestPasswordFieldLayout?.saveCardPasswordToggle?.setOnClickListener {
            if(isGuestPasswordVisible)
            {
                isGuestPasswordVisible=false
                binding?.guestPasswordFieldLayout?.saveCardPasswordToggle?.setImageResource(R.drawable.ic_eye_open)
                UiUtils.togglePasswordVisibility(false,binding?.guestPasswordFieldLayout?.etGuestPassword!!)
            }
            else
            {
                isGuestPasswordVisible=true
                binding?.guestPasswordFieldLayout?.saveCardPasswordToggle?.setImageResource(R.drawable.ic_pass_eye)
                UiUtils.togglePasswordVisibility(true,binding?.guestPasswordFieldLayout?.etGuestPassword!!)
            }

        }

        binding?.btnPayManually?.setOnClickListener {
            hideErrorMsg()
            IS_GUEST_USER=true
            binding?.phoneLayout?.let { expand(it) }
            binding?.passwordLayout?.visibility=View.GONE
            binding?.layoutSaveCard?.visibility=View.GONE
            binding?.btnPaySaveCard?.visibility=View.VISIBLE
        }

        binding?.btnPaySaveCard?.setOnClickListener {
            IS_GUEST_USER=false
            binding?.btnPaySaveCard?.visibility=View.GONE
            binding?.passwordLayout?.let { expand(it) }

            if(binding?.phoneLayout?.visibility==View.VISIBLE)
                binding?.phoneLayout?.let {
                    binding?.etPhoneNumber?.text?.clear()
                    collapse(it)
                }

            if(binding?.cardLayout?.visibility==View.VISIBLE)
                binding?.cardLayout?.let {
                    resetFieldVisibility()
                    collapse(it) }
        }


        binding?.etPhoneNumber?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.phoneLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
                binding?.phoneLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        }
        binding?.etPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.passwordFieldLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
                binding?.passwordFieldLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        }

        binding?.etEmail?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.etEmail?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
                binding?.etEmail?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        }

        binding?.etCardNumber?.setOnFocusChangeListener { _, hasFocus ->
            isCardFocused=hasFocus
            if (hasFocus)
                binding?.etCardNumber?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                typingCardDelayHandler?.removeCallbacksAndMessages(null)
                isCardNumberValid=validator.validationCard(binding?.etCardNumber!!)

                if(!isCardNumberValid)
                    binding?.etCardNumber?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etCardNumber?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)

            }

        }

        binding?.etExpiry?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.etExpiry?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                if(!isExpiryValid)
                    binding?.etExpiry?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etExpiry?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }
        }

        binding?.etCVC?.setOnFocusChangeListener { _, hasFocus ->
            isCVCFocused=hasFocus
            if (hasFocus)
                binding?.etCVC?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                typingDelayHandler?.removeCallbacksAndMessages(null)
                isCVCValid=validator.validationCVV(binding?.etCVC!!)
                if(!isCVCValid)
                    binding?.etCVC?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etCVC?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }
        }

        binding?.etFirstName?.setOnFocusChangeListener { _, hasFocus ->
            isFirstNameFocused=hasFocus
            if (hasFocus)
                binding?.etFirstName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                typingDelayHandler?.removeCallbacksAndMessages(null)
                isFirstNameValid = validator.validationName(binding?.etFirstName!!)
                if(!isFirstNameValid)
                    binding?.etFirstName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etFirstName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }

        }

        binding?.etLastName?.setOnFocusChangeListener { _, hasFocus ->
            isLastNameFocused=hasFocus
            if (hasFocus)
                binding?.etLastName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                typingDelayHandler?.removeCallbacksAndMessages(null)
                isLastNameValid = validator.validationName(binding?.etLastName!!)

                if(!isLastNameValid)
                    binding?.etLastName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etLastName?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }

        }

        binding?.etCity?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.etCity?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                if(!isCityValid)
                    binding?.etCity?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etCity?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }

        }


        binding?.guestPasswordFieldLayout?.etGuestPassword?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.guestPasswordFieldLayout?.guestPasswordLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                if(!isPasswordValid)
                    binding?.guestPasswordFieldLayout?.guestPasswordLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.guestPasswordFieldLayout?.guestPasswordLayout?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }

        }

        binding?.etStreetAddress?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.etStreetAddress?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
            {
                if(!isAddressValid)
                    binding?.etStreetAddress?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                else
                    binding?.etStreetAddress?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
            }

        }

        binding?.etPostalCode?.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus)
                binding?.etPostalCode?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
            else
                binding?.etPostalCode?.background = AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        }

        binding?.etEmail?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                email = s.toString().trim()
                // Validate email format
                debouncedValidateEmail(email)


            }
        })

        binding?.etCardNumber?.addTextChangedListener(object : TextWatcher {
            private var isUpdating: Boolean = false
            private val space = ' '

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingCardDelayHandler?.removeCallbacksAndMessages(null)

                typingCardDelayHandler = Handler(Looper.getMainLooper())
                if (isCardFocused) {
                    typingCardDelayHandler?.postDelayed({
                        isCardNumberValid=validator.validationCard(binding?.etCardNumber!!)
                        validateAndUpdateButtonState()

                    },500)
                }

            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdating) return

                isUpdating = true

                // Remove all spaces and get clean card number
                val cardNumber = s.toString().replace(" ", "")

                detectCardType(cardNumber)

                // Format the card number
                val formattedNumber = StringBuilder()

                for (i in cardNumber.indices) {
                    if (i > 0 && i % 4 == 0) {
                        formattedNumber.append(space)
                    }
                    formattedNumber.append(cardNumber[i])
                }

                // Set the formatted number to the EditText
                binding?.etCardNumber?.setText(formattedNumber.toString())
                binding?.etCardNumber?.setSelection(formattedNumber.length) // Move cursor to end
                isUpdating = false

            }
        })



        binding?.etPhoneNumber?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                //binding?.etPhoneNumber?.removeTextChangedListener(this)
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                //isFetchTrackerSuccess=false
                // No action needed here
                typingDelayHandler = Handler(Looper.getMainLooper())
                typingDelayHandler?.postDelayed({
                    // User has stopped typing for 500ms
                    if (!s.isNullOrEmpty()) {
                            if(binding?.etPhoneNumber?.text?.isEmpty()==true)
                            {
                                isFetchTrackerSuccess=false
                                return@postDelayed
                            }

                                isPhoneValid=validator.validatePhone(binding?.etPhoneNumber!!,binding?.phoneLayout!!,binding?.phone?.selectedCountryNameCode.toString())

                                if(isPhoneValid && binding?.cardLayout?.visibility==View.GONE)
                                {
                                    binding?.cardLayout?.let { expand(it) }
                                    validateAndUpdateButtonState()
                                }

                                else if(!isPhoneValid && binding?.cardLayout?.visibility==View.VISIBLE)
                                {
                                    //clearCardFields()
                                    binding?.cardLayout?.let {
                                        resetFieldVisibility()
                                        collapse(it) }
                                    enablePayButton(false,binding?.btnPay,mActivity)
                                }


                    }
                }, DEBOUNCE_DELAY)



            }
        })


        binding?.etFirstName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                if(typingDelayHandler==null)
                    typingDelayHandler = Handler(Looper.getMainLooper())

                typingDelayHandler?.postDelayed({
                    if (!s.isNullOrEmpty() && isFirstNameFocused) {
                        isFirstNameValid=validator.validationName(binding?.etFirstName!!)
                        validateAndUpdateButtonState()
                    }

                },500)
            }
        })

        binding?.etLastName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                if(typingDelayHandler==null)
                    typingDelayHandler = Handler(Looper.getMainLooper())

                typingDelayHandler?.postDelayed({
                    if (!s.isNullOrEmpty() && isLastNameFocused) {
                        isLastNameValid=validator.validationName(binding?.etLastName!!)
                        validateAndUpdateButtonState()
                    }

                },500)
            }
        })



        binding?.etPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                if(isProgrammaticChange)
                    return

                typingDelayHandler = Handler(Looper.getMainLooper())
                typingDelayHandler?.postDelayed({
                    isPasswordValid=validator.validatePassword(binding?.etPassword!!,binding?.passwordFieldLayout!!)
                },500)
            }
        })

        binding?.guestPasswordFieldLayout?.etGuestPassword?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                if(isProgrammaticChange)
                    return

                typingDelayHandler = Handler(Looper.getMainLooper())
                typingDelayHandler?.postDelayed({
                    isPasswordValid=validator.validatePassword(binding?.guestPasswordFieldLayout?.etGuestPassword!!,binding?.guestPasswordFieldLayout?.guestPasswordLayout!!)
                    Log.e("isPasswordValid", isPasswordValid.toString())
                    if(isPasswordValid)
                        validateAndUpdateButtonState()
                    else
                        enablePayButton(isEnable = false,binding?.btnPay,mActivity)
                },500)
            }
        })

        binding?.etCity?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                //typingDelayHandler?.removeCallbacksAndMessages(null)
                if(isProgrammaticChange)
                    return

                isCityValid=validator.validationName(binding?.etCity!!)
                validateAndUpdateButtonState()

            }

            override fun afterTextChanged(s: Editable?) {
               /*     typingDelayHandler = Handler(Looper.getMainLooper())
                    typingDelayHandler?.postDelayed({
                        if (!s.isNullOrEmpty()) {
                            isCityValid=validator.validationName(binding?.etCity!!)
                            validateAndUpdateButtonState()
                        }

                    },500)*/
                }
        })

        binding?.etStreetAddress?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Validate phone input as the user types
                /*typingDelayHandler?.removeCallbacksAndMessages(null)
                typingDelayHandler = Handler(Looper.getMainLooper())
                typingDelayHandler?.postDelayed({
                    if (!s.isNullOrEmpty()) {
                        isAddressValid=validator.validationStreetAddress(binding?.etStreetAddress!!)
                        validateAndUpdateButtonState()
                    }

                },500)*/
                if(isProgrammaticChange)
                    return

                isAddressValid=validator.validationStreetAddress(binding?.etStreetAddress!!)
                validateAndUpdateButtonState()

            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding?.etPostalCode?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isProgrammaticChange)
                    return

                isPostalCodeValid=validator.validationPostalCode(binding?.etPostalCode!!)
                validateAndUpdateButtonState()

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

        binding?.etCVC?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No action needed here
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                typingDelayHandler?.removeCallbacksAndMessages(null)

            }

            override fun afterTextChanged(s: Editable?) {
                if(typingDelayHandler==null)
                    typingDelayHandler = Handler(Looper.getMainLooper())

                typingDelayHandler?.postDelayed({
                    if (!s.isNullOrEmpty() && isCVCFocused) {
                        isCVCValid=validator.validationCVV(binding?.etCVC!!)
                        validateAndUpdateButtonState()
                    }

                },500)
            }
        })

        binding?.etExpiry?.addTextChangedListener(object : TextWatcher {
            var isDeleting: Boolean = false

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                isDeleting = count > after
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                val input = s.toString()
                var isMonthValid = false
                var isYearValid = false
                if (!isDeleting && input.length == 2) {
                    s?.append("/")
                }

                val parts = input.split("/")
                if (parts.size == 2) {
                     month = parts[0]
                    year = parts[1]

                    // Validate month
                    if (month.length == 2 && month.toIntOrNull()?.let { it in 1..12 } == false) {
                        isMonthValid=false
                    }
                    else
                    {
                        isMonthValid=true
                    }

                    // Validate year
                    if (year.length == 2 && year.toIntOrNull() != null) {
                        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
                        val fullYear = convertToFourDigitYear(year.toInt())
                        if (year.toInt() < currentYear) {
                            isYearValid=false
                        }
                        else
                        {
                            isYearValid=true
                        }

                        year=fullYear.toString()

                    }

                    if(isYearValid && isMonthValid)
                    {
                        isExpiryValid=true
                        binding?.etExpiry?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_focused)
                    }
                    else
                    {
                        isExpiryValid=false
                        binding?.etExpiry?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_error)
                    }

                    validateAndUpdateButtonState()
                }
            }
        })
    }

    private fun convertToFourDigitYear(twoDigitYear: Int): Int {
        val currentCentury = Calendar.getInstance().get(Calendar.YEAR) / 100 * 100
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100

        return if (twoDigitYear < currentYear) {
            currentCentury + 100 + twoDigitYear  // Next century
        } else {
            currentCentury + twoDigitYear  // Current century
        }
        }

    private fun validateAndUpdateButtonState() {
       /* if(viewModel.isPaymentMethodClicked)
        {
            if (binding?.etCardNumber?.text?.isNotEmpty()==true
                && binding?.etExpiry?.text?.isNotEmpty()==true && binding?.etCVC?.text?.isNotEmpty()==true &&
                binding?.etFirstName?.text?.isNotEmpty()==true && binding?.etLastName?.text?.isNotEmpty()==true
                && binding?.etCity?.text?.isNotEmpty()==true && binding?.etStreetAddress?.text?.isNotEmpty()==true && countryCode.isNotEmpty()) {
                enablePayButton(true,binding?.btnPay,mActivity)
            }
            else
            {
                enablePayButton(false,binding?.btnPay,mActivity)
            }
        }
        else
        {
            if(viewModel.isCheckedSavedCard)
            {
                if (binding?.guestPasswordFieldLayout?.etGuestPassword?.text?.isNotEmpty()==true && binding?.etPhoneNumber?.text?.isNotEmpty()==true && binding?.etCardNumber?.text?.isNotEmpty()==true
                    && binding?.etExpiry?.text?.isNotEmpty()==true && binding?.etCVC?.text?.isNotEmpty()==true &&
                    binding?.etFirstName?.text?.isNotEmpty()==true && binding?.etLastName?.text?.isNotEmpty()==true
                    && binding?.etCity?.text?.isNotEmpty()==true && binding?.etStreetAddress?.text?.isNotEmpty()==true && countryCode.isNotEmpty()) {
                    enablePayButton(true,binding?.btnPay,mActivity)
                }
                else
                {
                    enablePayButton(false,binding?.btnPay,mActivity)
                }
            }
            else
            {
                if (binding?.etPhoneNumber?.text?.isNotEmpty()==true && binding?.etCardNumber?.text?.isNotEmpty()==true
                    && binding?.etExpiry?.text?.isNotEmpty()==true && binding?.etCVC?.text?.isNotEmpty()==true &&
                    binding?.etFirstName?.text?.isNotEmpty()==true && binding?.etLastName?.text?.isNotEmpty()==true
                    && binding?.etCity?.text?.isNotEmpty()==true && binding?.etStreetAddress?.text?.isNotEmpty()==true && countryCode.isNotEmpty()) {
                    enablePayButton(true,binding?.btnPay,mActivity)
                }
                else
                {
                    enablePayButton(false,binding?.btnPay,mActivity)
                }
            }

        }*/

        if(viewModel.isPaymentMethodClicked)
        {
            requiredFieldsViews = mutableListOf<AppCompatEditText?>().apply {
                add(binding?.etCardNumber)
                add(binding?.etExpiry)
                add(binding?.etCVC)
                add(binding?.etFirstName)
                add(binding?.etLastName)
                if(viewModel.isCheckedSavedCard) add(binding?.guestPasswordFieldLayout?.etGuestPassword) else remove(binding?.guestPasswordFieldLayout?.etGuestPassword)
                if(requiredFields.contains("Locality")) add(binding?.etCity)
                if (requiredFields.contains("StreetAddress")) add(binding?.etStreetAddress)
                if (requiredFields.contains("PostCode")) add(binding?.etPostalCode)
            }
        }
        else
        {
            requiredFieldsViews = mutableListOf<AppCompatEditText?>().apply {
                add(binding?.etPhoneNumber)
                add(binding?.etCardNumber)
                add(binding?.etExpiry)
                add(binding?.etCVC)
                add(binding?.etFirstName)
                add(binding?.etLastName)
                if(viewModel.isCheckedSavedCard) add(binding?.guestPasswordFieldLayout?.etGuestPassword) else remove(binding?.guestPasswordFieldLayout?.etGuestPassword)
                if(requiredFields.contains("Locality")) add(binding?.etCity)
                if (requiredFields.contains("StreetAddress")) add(binding?.etStreetAddress)
                if (requiredFields.contains("PostCode")) add(binding?.etPostalCode)
            }
        }


        requiredFieldsViews.forEach { editText ->
            editText?.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    // Check all required fields after every change
                    Log.e("Called", "afterTextChanged")
                    if(typingDelayHandler==null)
                        typingDelayHandler = Handler(Looper.getMainLooper())

                    typingDelayHandler?.postDelayed({
                        enablePayButton(areAllRequiredFieldsFilled(), binding?.btnPay, mActivity)
                    },500)

                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    Log.e("Called", "onTextChanged")
                    typingDelayHandler?.removeCallbacksAndMessages(null)
                }
            })
        }


    }

    private fun detectCardType(cardNumber: String) {
        val cardType = CardType.fromCardNumber(cardNumber)
        binding?.cardTypeImageView?.setImageDrawable(getDrawable(cardType.drawableResId))
    }

    private fun getDrawable(drawableResId: Int): Drawable? {
        return ContextCompat.getDrawable(binding?.cardLayout?.context!!, drawableResId)
    }

    private fun callAPI(apiCall:String)
    {
        when(apiCall)
        {
            API_CALL_FETCH_TRACKER->callFetchTrackerAPI()
            API_CALL_USER_EXIST->callCheckUserExistAPI()
            API_CALL_GENERATE_TOKEN->callGenerateTokenAPI()
            API_CALL_SHOPPER_TOKEN->callShopperTokenAPI()
            API_CALL_SHOPPER_LOGIN->callShopperLoginAPI()
            API_CALL_PAYER_AUTHENTICATION-> callPayerAuthenticationAPI()
            API_CALL_PAYER_AUTHENTICATION_SAVED_CARD->callPayerAuthenticationSavedCardAPI()
            API_CALL_ENROLLMENT-> callEnrollmentAPI(viewModel.consumerSessionId)
            API_CALL_AUTHORIZATION-> callAuthorizationAPI()
            API_CALL_UN_ENROLL_CARD-> callUnEnrolledAuthorizationAPI()
            API_CALL_CAPTURE-> callCaptureAPI()
            API_CALL_CREATE_SAFEPAY_SHOPPER-> callCreateShopperUserAPI()
            API_CALL_GET_ADDRESS->callGetAddressAPI()
            API_CALL_GET_PAYMENT_METHOD_LIST->callGetPaymentMethodList()
            API_CALL_GET_PRE_FETCH_ADDRESS->callFetchAddressAPI()

        }
    }

    /******************************API CALLING*****************************************************/

    private fun callCheckUserExistAPI()
    {
        if(binding?.phoneLayout?.visibility==View.VISIBLE)
            binding?.phoneLayout?.let {
                binding?.etPhoneNumber?.text?.clear()
                collapse(it) }

        if(binding?.passwordLayout?.visibility==View.VISIBLE)
            binding?.passwordLayout?.let { collapse(it) }

        showEmailLoading()
        viewModel.checkUser(email,this)
    }

    private fun callPayerAuthenticationAPI()
    {
        val card = PayerAuthenticationRequest.Card(cardNumber = binding?.etCardNumber?.text?.toString().toString(),
            expirationMonth = month, expirationYear = year, cvv = binding?.etCVC?.text?.toString().toString()
        )

        val paymentMethod = PayerAuthenticationRequest.PaymentMethod(card = card)

        val payload = PayerAuthenticationRequest.Payload(isMobile = true,paymentMethod = paymentMethod)
        //val resetPayload = PayerAuthenticationRequest.TrackerResetPayload()
        val payerAuthenticationRequest = PayerAuthenticationRequest(entryMode = "raw", payload = payload,
            action = "PAYER_AUTH_SETUP"
        )

        viewModel.payerAuthenticationSetup(tracker = configuration.trackerToken,payerAuthenticationRequest=payerAuthenticationRequest,this)
    }

    private fun callPayerAuthenticationSavedCardAPI()
    {
        val tokenizedCard = PayerAuthenticationRequest.TokenizedCard(viewModel.savedCardToken)

        val paymentMethod = PayerAuthenticationRequest.PaymentMethod(tokenizedCard = tokenizedCard)

        val payload = PayerAuthenticationRequest.Payload(isMobile = true,paymentMethod = paymentMethod)
        //val resetPayload = PayerAuthenticationRequest.TrackerResetPayload()
        val payerAuthenticationRequest = PayerAuthenticationRequest(entryMode = "tms",payload,"PAYER_AUTH_SETUP")

        viewModel.payerAuthenticationSetup(tracker = configuration.trackerToken,payerAuthenticationRequest=payerAuthenticationRequest,this)

    }


    private fun callEnrollmentAPI(consumerSessionID:String?)
    {
        val billing = Billing(binding?.etStreetAddress?.text.toString(),"",binding?.etCity?.text.toString(),state,binding?.etPostalCode?.text.toString(),countryCode)

        val doCardOnFile = if (viewModel.isCheckedSavedCard) true else false
        val authorization = Authorization(doCapture = false,doCardOnFile)

        val authenticationSetup = AuthenticationSetup(
            successUrl = "https://safepay-sdk",
            failureUrl = "https://safepay-sdk",
            "",
            consumerSessionID.toString()
        )

        val payload = Payload(
            billing = billing,
            authorization = authorization,
            authenticationSetup = authenticationSetup
        )

        val enrollmentRequest=EnrollmentRequest(payload)
        viewModel.enrollment(configuration.trackerToken,enrollmentRequest,this)

    }

    private fun callAuthorizationAPI()
    {
        val doCardOnFile = if (viewModel.isCheckedSavedCard) true else false

        val authorizationRequest=AuthorizationRequest(AuthorizationRequest.Payload(AuthorizationRequest.Authorization(false,viewModel.sdkOnValidateJWT?:"",doCardOnFile),true))
        viewModel.authorization(configuration.trackerToken,authorizationRequest,this)

    }

    private fun callUnEnrolledAuthorizationAPI()
    {
        val authorizationRequest= UnEnrollmentCardAuthorizationRequest(UnEnrollmentCardAuthorizationRequest.Payload(UnEnrollmentCardAuthorizationRequest.Authorization(false),true))
        viewModel.unEnrollAuthorization(configuration.trackerToken,authorizationRequest,this)

    }

    private fun callCaptureAPI()
    {
        val requestBody= createEmptyJsonRequestBody()
        viewModel.capture(configuration.trackerToken,requestBody,this)

    }
    private fun callCreateShopperUserAPI()
    {
        val shopperUserRequest=ShopperUserRequest(binding?.etFirstName?.text?.toString(),
        binding?.etLastName?.text?.toString(),email,
        binding?.phone?.fullNumber,binding?.guestPasswordFieldLayout?.etGuestPassword?.text?.toString())

        viewModel.createShopperUser(shopperUserRequest,this)
    }

    private fun callGetAddressAPI()
    {
        viewModel.getAddress(countryCode,this)
    }

    private fun callFetchAddressAPI()
    {
        if(configuration.addressToken?.isNotEmpty()==true)
            viewModel.fetchAddress(configuration.addressToken.toString(),configuration.timeBaseToken,this)
    }

    private fun callGetPaymentMethodList()
    {
        viewModel.getPaymentMethodList(this)
    }

    private fun createEmptyJsonRequestBody(): RequestBody {
        return "{}".toRequestBody("application/json".toMediaType())
    }

    private fun callFetchTrackerAPI()
    {
        showTrackerLoading()
        hideViews()
        if(configuration.validateConfiguration())
            viewModel.fetchTracker(configuration.trackerToken,configuration.timeBaseToken,this)
        else
        {
            hideTrackerLoading()
            showErrorMsg(PaymentError.invalidConfiguration().msg)
        }

    }

    private fun callGenerateTokenAPI()
    {
        val tokenGenerateRequest = TokenGenerateRequest(binding?.etFirstName?.text?.toString().toString(),
            binding?.etLastName?.text?.toString().toString(),
            email,binding?.phone?.fullNumber.toString(),countryCode)
        viewModel.generateToken(tokenGenerateRequest,this)
    }

    private fun callShopperTokenAPI()
    {
        val shopperTokenGenerateRequest = ShopperTokenGenerateRequest("password",binding?.etEmail?.text.toString(),binding?.guestPasswordFieldLayout?.etGuestPassword?.text.toString())
        viewModel.generateShopperToken(shopperTokenGenerateRequest,this)
    }

    private fun callShopperLoginAPI()
    {
        val shopperTokenGenerateRequest = ShopperTokenGenerateRequest("password",binding?.etEmail?.text.toString(),binding?.etPassword?.text.toString())
        viewModel.generateShopperToken(shopperTokenGenerateRequest,this)
    }

    private fun setSavedCardsData(items: List<PaymentData>)
    {

        binding?.layoutPaymentMethod?.recycler?.layoutManager = LinearLayoutManager(mActivity)
        val adapter = SavedCardAdapter((items),this)
        binding?.layoutPaymentMethod?.recycler?.adapter = adapter
    }
    /******************************API CALLING*****************************************************/

    private fun hideViews()
    {
        binding?.etEmail?.visibility=View.GONE
        binding?.btnPayLayout?.visibility=View.GONE
    }

    private fun showViews()
    {
        binding?.etEmail?.visibility=View.VISIBLE
        binding?.btnPayLayout?.visibility=View.VISIBLE
    }

    private fun reset()
    {
        binding?.guestPasswordFieldLayout?.root?.let { collapse(it) }
        binding?.phoneLayout?.let {
            isProgrammaticChange=true
            binding?.etPhoneNumber?.text?.clear()
            isProgrammaticChange=false
            collapse(it)}
        binding?.passwordLayout?.let {collapse(it)}
        binding?.cardLayout?.let {
            resetFieldVisibility()
            collapse(it) }
        viewModel.isPaymentMethodClicked=false
        viewModel.isPaymentSuccess=false
        viewModel.payment=Payment.CANCEL

        clearAddressFields()
        viewModel.isCheckedSavedCard=false
        viewModel.defaultCountry=""
        viewModel.setResponse(null)
        hideViews()
        countryCode=""
        email=""
        month=""
        year=""
        isPhoneValid = false
        isPasswordValid = false
        isCVCValid = false
        isExpiryValid = false
        isCardNumberValid = false
        isCityValid = false
        isPostalCodeValid = false
        isAddressValid = false
        isFirstNameValid = false
        isLastNameValid = false
        typingDelayHandler?.removeCallbacksAndMessages(null)
        typingCardDelayHandler?.removeCallbacksAndMessages(null)

    }

    private fun showLoading()
    {
        binding?.txtError?.visibility=View.GONE
        binding?.progressBar?.visibility=View.VISIBLE
        binding?.btnPay?.text = ""
        binding?.btnPay?.isEnabled=false
    }
    private fun hideLoading()
    {
        binding?.btnPay?.isEnabled=true
        binding?.progressBar?.visibility=View.GONE
    }

    private fun showPasswordLoading()
    {
        binding?.btnLogin?.isEnabled=false
       binding?.progressBarPassword?.visibility=View.VISIBLE
    }
    private fun hidePasswordLoading()
    {
        binding?.btnLogin?.isEnabled=true
        binding?.progressBarPassword?.visibility=View.GONE
    }

    private fun showEmailLoading()
    {
        binding?.emailProgress?.visibility=View.VISIBLE
    }
    private fun hideEmailLoading()
    {
        binding?.emailProgress?.visibility=View.GONE
    }

    private fun showTrackerLoading()
    {
        binding?.trackerProgress?.visibility=View.VISIBLE
    }
    private fun hideTrackerLoading()
    {
        binding?.trackerProgress?.visibility=View.GONE
    }

    private fun showErrorMsg(message:String)
    {
        binding?.txtError?.visibility=View.VISIBLE
        binding?.txtError?.text=message
    }
    private fun hideErrorMsg()
    {
        binding?.txtError?.visibility=View.GONE
    }

    private fun initCardinalSDK()
    {
        cardinal= com.cardinalcommerce.cardinalmobilesdk.Cardinal.getInstance()
        val cardinalConfigurationParameters = CardinalConfigurationParameters()
        if(configuration.sandBox)
            cardinalConfigurationParameters.environment = CardinalEnvironment.PRODUCTION
        else
            cardinalConfigurationParameters.environment = CardinalEnvironment.STAGING

        cardinalConfigurationParameters.requestTimeout = 8000
        cardinalConfigurationParameters.challengeTimeout = 5

        val rTYPE = JSONArray()
        rTYPE.put(CardinalRenderType.OTP)
        cardinalConfigurationParameters.renderType = rTYPE
        cardinalConfigurationParameters.uiType = CardinalUiType.BOTH
        val yourUICustomizationObject = UiCustomization()
        cardinalConfigurationParameters.uiCustomization = yourUICustomizationObject
        cardinal.configure(mActivity, cardinalConfigurationParameters)
    }

    override fun onSuccess(response: Any?) {
        when(response)
        {
            is UserExistsResponse->
            {
                IS_GUEST_USER = !response.data.exists
                hideEmailLoading()
                if(IS_GUEST_USER)
                {
                    binding?.layoutSaveCard?.visibility=View.VISIBLE
                    binding?.phoneLayout?.let { expand(it) }
                    if(binding?.cardLayout?.visibility==View.VISIBLE)
                    {
                        binding?.cardLayout?.let {
                            resetFieldVisibility()
                            collapse(it) }
                    }
                }

                else
                {
                    binding?.layoutSaveCard?.visibility=View.GONE
                    if(binding?.cardLayout?.visibility==View.VISIBLE)
                    {
                        binding?.cardLayout?.let {
                            resetFieldVisibility()
                            collapse(it) }
                    }

                    binding?.passwordLayout?.let { expand(it) }
                }

            }
            is ShopperUserResponse->
            {
                callAPI(API_CALL_SHOPPER_TOKEN)
            }
            is GetAddressResponse->
            {
                clearAddressFields()
                requiredFields= response.data.requiredFields
                val fieldsMap = mapOf(
                    "Locality" to binding?.etCity,
                    "StreetAddress" to binding?.etStreetAddress,
                    "PostCode" to binding?.etPostalCode
                )
                fieldsMap.forEach { (fieldName, editText) ->
                    if (requiredFields.contains(fieldName)) {
                        editText?.visibility=View.VISIBLE
                        //editText?.let { expand(it) }
                    } else {
                        editText?.visibility=View.GONE
                    }
                }
                if(requiredFields.contains("AdministrativeArea"))
                {
                    if(response.data.administrativeArea.options.size>0)
                    {
                        binding?.etState?.visibility=View.GONE
                        binding?.spinnerState?.visibility=View.VISIBLE
                        state= response.data.administrativeArea.options[0].name
                        spinnerAdapter.setDropDownViewResource(R.layout.spinner_item)
                        spinnerAdapter.clear()
                        binding?.spinnerState?.adapter = spinnerAdapter
                        binding?.spinnerState?.prompt="Select "+response.data.administrativeArea.name
                        stateOptionItems=response.data.administrativeArea.options
                        spinnerAdapter.addAll(stateOptionItems.map { it.name })
                        spinnerAdapter.notifyDataSetChanged()

                        binding?.spinnerState?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                val selectedItem = stateOptionItems[position]
                                val selectedId = selectedItem.id
                                val selectedName = selectedItem.name
                                state=selectedName
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                            }
                        }
                    }
                    else
                    {
                        //show state Field
                        binding?.etState?.visibility=View.VISIBLE
                        state=binding?.etState?.text.toString()
                    }

                }
                else
                {
                    state=""
                    binding?.spinnerState?.visibility=View.GONE
                }

                showSaveAddress()
                validateAndUpdateButtonState()


            }
            is FetchAddressResponse->
            {
                viewModel.setResponse(response)
                if(response.data?.city?.isNotEmpty()==true)
                {
                    binding?.etCity?.setText(response.data.city.toString())
                    binding?.etCity?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
                }
                if(response.data?.street1?.isNotEmpty()==true)
                {
                    binding?.etStreetAddress?.setText(response.data.street1.toString())
                    binding?.etStreetAddress?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
                }
                if(response.data?.postalCode?.isNotEmpty()==true)
                {
                    binding?.etPostalCode?.setText(response.data.postalCode.toString())
                    binding?.etPostalCode?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
                }

                if(response.data?.state?.isNotEmpty()==true)
                {
                    state=response.data.state
                    val position=spinnerAdapter.getPosition(state)
                    binding?.spinnerState?.setSelection(position)

                }
            }
            is TokenResponse->
            {
                hidePasswordLoading()
                if(IS_GUEST_USER)
                    callAPI(API_CALL_PAYER_AUTHENTICATION)
                else
                    callAPI(API_CALL_GET_PAYMENT_METHOD_LIST)
            }

            is PaymentMethodListResponse->
            {

                if(response.data.size>0)
                {
                    binding?.etEmail?.isEnabled=false
                    viewModel.savedCardToken= response.data[0].token
                    setSavedCardsData(response.data)
                    enablePayButton(true,binding?.btnPay,mActivity)
                    binding?.layoutPaymentMethod?.root?.let { expand(it) }
                    binding?.passwordLayout?.visibility=View.GONE
                }
                else
                {
                    hideErrorMsg()
                    viewModel.isPaymentMethodClicked=true
                    binding?.tvHeadingCard?.text="Add a payment method"
                    binding?.tvCardDetailCancel?.visibility=View.GONE
                    binding?.layoutSaveCard?.visibility=View.GONE
                    binding?.layoutPaymentMethod?.root?.visibility=View.GONE
                    binding?.cardLayout?.let { expand(it) }
                    binding?.passwordLayout?.visibility=View.GONE
                    validateAndUpdateButtonState()
                }

            }
            is PayerAuthenticationResponse->
            {
                cardinal.init(response.data.action.payer_authentication_setup.cardinalJWT, object : CardinalInitService {
                    override fun onSetupCompleted(consumerSessionId: String?) {
                        Log.e("CARDINAL_JWT", response.data.action.payer_authentication_setup.cardinalJWT.toString())
                        viewModel.consumerSessionId=consumerSessionId
                        callAPI(API_CALL_ENROLLMENT)
                    }
                    override fun onValidated(validateResponse: ValidateResponse?, p1: String?) {
                        hideLoading()
                        Log.e("CARDINAL onValidated", validateResponse?.errorDescription.toString())


                    }
                })
            }

            is AuthorizationResponse ->
            {
                //SHOW PAYMENT_SUCCESS DIALOG
                when(response.data.tracker.next_actions.CYBERSOURCE.kind)
                {
                    NextActions.CAPTURE->callCaptureAPI()
                    else->
                    {
                        hideBlockerView(binding?.overlay)
                        reset()
                        hideLoading()
                        paymentResultCallback?.invoke(PaymentResult.Completed)

                        binding?.scroll?.visibility=View.GONE
                        binding?.layoutPaymentSuccess?.mainContainer?.visibility = View.VISIBLE
                        viewModel.isPaymentSuccess=true
                        viewModel.payment=Payment.SUCCESS
                        /*val coroutineScope = CoroutineScope(Dispatchers.Main)

                        coroutineScope.launch {
                            delay(DEBOUNCE_DELAY) // Delay for 500 milliseconds
                            val slideIn = AnimationUtils.loadAnimation(mActivity, R.anim.slide_out_bottom)
                            binding?.layoutPaymentSuccess?.mainContainer?.startAnimation(slideIn)
                            binding?.layoutPaymentSuccess?.mainContainer?.visibility = View.VISIBLE
                        }*/
                    }
                }
            }

            is TrackerFetchResponse->
            {
                when (response.data.state) {
                    TrackerStatus.TRACKER_STARTED, TrackerStatus.TRACKER_AUTHORIZED, TrackerStatus.TRACKER_ENROLLED -> {
                        isFetchTrackerSuccess=true
                        showViews()
                        hideTrackerLoading()
                        val country= CurrencyToCountryUtil.countryAndCode(response.data.purchaseTotals.quoteAmount.currency)
                        currencySymbol = CurrencyUtil.symbolFor(response.data.purchaseTotals.quoteAmount.currency)
                        amount = CurrencyUtil.convertMinorToMajor(response.data.purchaseTotals.quoteAmount.currency, response.data.purchaseTotals.quoteAmount.amount).toString()
                        binding?.phone?.setCountryForNameCode(country.second)
                        binding?.countryCode?.setCountryForNameCode(country.second)
                        countryCode=binding?.countryCode?.selectedCountryNameCode.toString()
                        viewModel.defaultCountry=countryCode
                        binding?.btnPay?.text = "Pay $currencySymbol.$amount"
                        binding?.phone?.registerCarrierNumberEditText(binding?.etPhoneNumber)
                        callAPI(API_CALL_GET_PRE_FETCH_ADDRESS)
                    }
                    else-> {
                        hideTrackerLoading()
                        onFailure(PaymentError.invalidTrackerState().msg)
                        //showErrorMsg(PaymentError.invalidTrackerState().msg)
                        paymentResultCallback?.invoke(PaymentResult.Failed(PaymentError.invalidTrackerState()))
                    }
                }
            }

            is EnrollmentResponse->
            {
                when(response.data.action.payerAuthenticationEnrollment.authenticationStatus)
                {
                    EnrollmentStatus.REQUIRED->
                    {
                        try {
                            cardinal.cca_continue(response.data.action.payerAuthenticationEnrollment.transactionId,
                                response.data.action.payerAuthenticationEnrollment.payload,
                                mActivity,object: CardinalValidateReceiver {
                                    override fun onValidated(
                                        context: Context?,
                                        validateResponse: ValidateResponse,
                                        responseString: String?
                                    ) {
                                        when (validateResponse.actionCode!!) {
                                            CardinalActionCode.SUCCESS -> {
                                                // handle logic for Action code SUCCESS
                                                DebugLogger.e("CARDINAL SUCCESS")
                                                viewModel.sdkOnValidateJWT=responseString

                                                callAPI(API_CALL_AUTHORIZATION)
                                            }

                                            CardinalActionCode.CANCEL -> {
                                                onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                                                DebugLogger.e("CARDINAL CANCEL")
                                            }

                                            CardinalActionCode.NOACTION -> {
                                                onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                                                DebugLogger.e("CARDINAL NOACTION")
                                                // handle logic for Action code NOACTION
                                            }

                                            CardinalActionCode.FAILURE -> {
                                                onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                                                DebugLogger.e("CARDINAL FAILURE")
                                                // handle logic for Action code FAILURE
                                            }

                                            CardinalActionCode.ERROR -> {
                                                onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                                                DebugLogger.e("CARDINAL ERROR")
                                                // handle logic for Action code ERROR
                                            }

                                            CardinalActionCode.TIMEOUT -> {
                                                onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                                                DebugLogger.e("CARDINAL TIMEOUT")
                                                // handle logic for Action code TIMEOUT
                                            }
                                        }
                                    }
                                })
                        } catch (e: Exception) {
                            Log.e("Cardinal Logs", "Exception "+e.message)
                            onFailure("Failed to authenticate. Your card issuer cannot authenticate this card. Please select another card or form of payment to complete your purchase.")
                            // Handle exception
                        }
                    }
                    EnrollmentStatus.FRICTIONLESS,EnrollmentStatus.ATTEMPTED->
                    {
                        callAPI(API_CALL_UN_ENROLL_CARD)
                    }
                   /* EnrollmentStatus.ATTEMPTED->
                    {
                        callAPI(API_CALL_UN_ENROLL_CARD)
                    }*/
                    else->
                    {
                        showErrorMsg(ContextCompat.getString(mActivity,R.string.error_msg))
                    }
                   /* EnrollmentStatus.UNAVAILABLE->
                    {
                        showErrorMsg(ContextCompat.getString(mActivity,R.string.error_msg))
                    }

                    EnrollmentStatus.FAILED->
                    {
                        showErrorMsg(ContextCompat.getString(mActivity,R.string.error_msg))
                    }
                    EnrollmentStatus.REJECTED->
                    {
                        showErrorMsg(ContextCompat.getString(mActivity,R.string.error_msg))
                    }

                    EnrollmentStatus.NOT_ELIGIBLE->
                    {
                        showErrorMsg(ContextCompat.getString(mActivity,R.string.error_msg))
                    }*/
                }
            }
        }
    }

    override fun onFailure(error: String?) {
        if(viewModel.isPaymentMethodClicked)
            viewModel.isCheckedSavedCard=false

        hideBlockerView(binding?.overlay)
        showButtonDefault()
        showErrorMsg(error.toString())
        DebugLogger.e(error.toString())
        viewModel.isPaymentSuccess=false
        viewModel.payment = Payment.FAILED
        paymentResultCallback?.invoke(PaymentResult.Failed(PaymentError("",error.toString())))
        if(binding?.progressBarPassword?.visibility==View.VISIBLE)
            hidePasswordLoading()
        if(binding?.trackerProgress?.visibility==View.VISIBLE)
            hideTrackerLoading()

        if(binding?.progressBar?.visibility==View.VISIBLE)
            hideLoading()

        hideEmailLoading()
    }

    override fun onClick(cardToken: String) {
        viewModel.savedCardToken=cardToken
    }

    private fun clearCardFields()
    {
        binding?.etCardNumber?.text?.clear()
        binding?.etCVC?.text?.clear()
        binding?.etExpiry?.text?.clear()
        binding?.etCity?.text?.clear()
        binding?.etStreetAddress?.text?.clear()
        binding?.etFirstName?.text?.clear()
        binding?.etLastName?.text?.clear()

        binding?.etCardNumber?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etCity?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etStreetAddress?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etPostalCode?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etCardNumber?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etCVC?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etFirstName?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etLastName?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etExpiry?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        showSaveAddress()
    }

    private fun clearAddressFields()
    {
        binding?.etStreetAddress?.text?.clear()
        binding?.etCity?.text?.clear()
        binding?.etPostalCode?.text?.clear()
        binding?.etCity?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etStreetAddress?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
        binding?.etPostalCode?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
    }

    private fun showButtonDefault()
    {
        binding?.btnLogin?.text="Login"
        binding?.btnPay?.text = "Pay $currencySymbol.$amount"
    }

    fun areAllRequiredFieldsFilled(): Boolean {
        val visibleFields = requiredFieldsViews.filter { it?.visibility == View.VISIBLE }

        return visibleFields.all { field ->
            val isFieldValid = when (field?.id) {
                R.id.etCity -> isCityValid // Check city validity only if `etCity` is visible
                R.id.etPostalCode -> isPostalCodeValid // Check postal code validity
                R.id.etStreetAddress -> isAddressValid // Check address validity
                else -> field?.text?.isNotEmpty() == true // Default check for other fields
            }

            // Combine field-specific validity with global conditions
            isFieldValid && isFirstNameValid && isLastNameValid && isCardNumberValid && isExpiryValid && isCVCValid } && (!viewModel.isCheckedSavedCard || isPasswordValid)
       /* return if(viewModel.isCheckedSavedCard) {
            requiredFieldsViews.all { it?.text?.isNotEmpty() == true && isPasswordValid  && isFirstNameValid && isLastNameValid && isCardNumberValid && isExpiryValid && isCVCValid}
        } else
            requiredFieldsViews.all { it?.text?.isNotEmpty() == true && isFirstNameValid && isLastNameValid && isCardNumberValid && isExpiryValid && isCVCValid}
*/    }

    private fun showSaveAddress()
    {
        if(countryCode==viewModel.defaultCountry)
        {
            if(viewModel.getResponse()!=null)
            {
                if(viewModel.getResponse()?.data?.city?.isNotEmpty()==true)
                {
                    binding?.etCity?.setText(viewModel.getResponse()?.data?.city.toString())
                    binding?.etCity?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)

                }
                if(viewModel.getResponse()?.data?.street1?.isNotEmpty()==true)
                {
                    binding?.etStreetAddress?.setText(viewModel.getResponse()?.data?.street1.toString())
                    binding?.etStreetAddress?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
                }
                if(viewModel.getResponse()?.data?.postalCode?.isNotEmpty()==true)
                {
                    binding?.etPostalCode?.setText(viewModel.getResponse()?.data?.postalCode.toString())
                    binding?.etPostalCode?.background=AppCompatResources.getDrawable(mActivity,R.drawable.bg_edittext)
                }

                if(viewModel.getResponse()?.data?.state?.isNotEmpty()==true)
                {
                    state=viewModel.getResponse()?.data?.state.toString()
                    val position=spinnerAdapter.getPosition(state)
                    binding?.spinnerState?.setSelection(position)
                }
            }
        }
    }

    fun <T> ((T) -> Unit).debounce(waitMs: Long): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            debounceJob?.cancel()
            debounceJob = CoroutineScope(Dispatchers.Main).launch {
                delay(waitMs)
                this@debounce(param)
            }
        }
    }



    private val validateEmail: (String) -> Unit = { email ->
        if (isValidEmail(email)) {
            if (NetworkUtil.isInternetConnected(mActivity)) {
                hideErrorMsg()
                clearEditTextIfNotEmpty(binding?.etPassword,
                    binding?.guestPasswordFieldLayout?.etGuestPassword,
                    binding?.etCardNumber,
                    binding?.etExpiry,
                    binding?.etCVC,
                    binding?.etFirstName,
                    binding?.etLastName,binding?.etCity,binding?.etStreetAddress,binding?.etPostalCode,binding?.etPhoneNumber)

                binding?.etEmail?.background = AppCompatResources.getDrawable(mActivity, R.drawable.bg_focused)
                callAPI(API_CALL_USER_EXIST) // Call the API directly here
            } else {
                Toast.makeText(mActivity, "Please check Internet", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (IS_GUEST_USER) {

                if(binding?.layoutSaveCard?.visibility==View.GONE)
                    binding?.layoutSaveCard?.visibility=View.VISIBLE

                if(binding?.cardLayout?.visibility==View.VISIBLE)
                    binding?.cardLayout?.let {
                        resetFieldVisibility()
                        collapse(it) }

                binding?.phoneLayout?.let {
                    binding?.etPhoneNumber?.text?.clear()
                    collapse(it) }
            } else {
                if(binding?.cardLayout?.visibility==View.VISIBLE)
                    binding?.cardLayout?.let {
                        resetFieldVisibility()
                        collapse(it) }

                binding?.passwordLayout?.let { collapse(it) }
            }

            if(binding?.btnPaySaveCard?.visibility==View.VISIBLE)
                binding?.btnPaySaveCard?.visibility=View.GONE

            binding?.etEmail?.background = AppCompatResources.getDrawable(mActivity, R.drawable.bg_error)
        }
    }

    private val debouncedValidateEmail = validateEmail.debounce(DEBOUNCE_DELAY)


    fun clearEditTextIfNotEmpty(vararg editTexts: AppCompatEditText?) {
        //reset()
         isPhoneValid = false
         isPasswordValid = false
         isCVCValid = false
         isExpiryValid = false
         isCardNumberValid = false
         isCityValid = false
         isPostalCodeValid = false
         isAddressValid = false
         isFirstNameValid = false
         isLastNameValid = false

        editTexts.forEach { editText ->
            if (editText?.text?.isNotEmpty() == true) {
                isProgrammaticChange=true
                editText.text?.clear()
                isProgrammaticChange = false
            }
        }
    }

    private fun resetFieldVisibility()
    {
        if(binding?.passwordLayout?.visibility==View.VISIBLE)
            binding?.passwordLayout?.visibility=View.GONE

        if(binding?.guestPasswordFieldLayout?.root?.visibility==View.VISIBLE)
            binding?.guestPasswordFieldLayout?.root?.visibility=View.GONE

        if(binding?.btnPaySaveCard?.visibility==View.VISIBLE)
            binding?.btnPaySaveCard?.visibility=View.GONE

        binding?.cbSaveCard?.isChecked=false

    }
}