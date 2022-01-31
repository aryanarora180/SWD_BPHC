package `in`.ac.bits_hyderabad.swd.swd.view.login

import `in`.ac.bits_hyderabad.swd.swd.MainActivity
import `in`.ac.bits_hyderabad.swd.swd.R
import `in`.ac.bits_hyderabad.swd.swd.data.SingleLiveEvent
import `in`.ac.bits_hyderabad.swd.swd.view.showSnackbarError
import `in`.ac.bits_hyderabad.swd.swd.viewmodel.LoginViewModel
import android.content.Intent
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.tasks.Task
import com.google.android.material.composethemeadapter.MdcTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private val viewModel by viewModels<LoginViewModel>()

    private lateinit var _googleSignInClient: GoogleSignInClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = ComposeView(requireContext()).apply {
        setContent {
            LoginScreen(
                viewModel = viewModel,
                onGoogleSignIn = {
                    viewModel.isGoogleSigningIn.value = true
                    googleSignInActivityResult.launch(_googleSignInClient.signInIntent)
                },
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _googleSignInClient =
            GoogleSignIn.getClient(requireContext(), viewModel.googleSignInOptions)

        viewModel.isAuthenticated.observe(viewLifecycleOwner, isAuthenticatedObserver)
        viewModel.needsProfileCompletion.observe(viewLifecycleOwner, needsProfileCompletionObserver)
        viewModel.onMessage.observe(viewLifecycleOwner, onMessageObserver)
    }

    private val googleSignInActivityResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            handleSignInResult(task)
        }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        if (completedTask.isSuccessful) {
            viewModel.signInWithGoogle(completedTask.result?.idToken ?: "")
        } else {
            viewModel.isGoogleSigningIn.value = false
            requireView().showSnackbarError("Google sign in failed. Please try again later")
            completedTask.exception?.printStackTrace()
        }
    }

    private val isAuthenticatedObserver = Observer<Boolean> { isAuthenticated ->
        if (isAuthenticated) {
            startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        }
    }

    private val needsProfileCompletionObserver = Observer<Boolean> { needsProfileCompletion ->
        if (needsProfileCompletion) {
            findNavController().navigate(R.id.action_loginFragment_to_profileFragment)
        }
    }

    private val onMessageObserver = Observer<SingleLiveEvent<String>> { error ->
        requireView().showSnackbarError(error)
    }
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel(), onGoogleSignIn: () -> Unit) {
    val isGoogleSigningIn by viewModel.isGoogleSigningIn
    val isCredentialsSigningIn by viewModel.isCredentialsSigningIn
    val isInForgotPasswordFlow by viewModel.showForgotPasswordDialog

    val context = LocalContext.current
    val intent = Intent(
        Intent.ACTION_SENDTO,
        Uri.fromParts("mailto", context.getString(R.string.swd_email_id), null)
    )

    val (uid, setUid) = remember { mutableStateOf("") }
    val (password, setPassword) = remember { mutableStateOf("") }

    var isPasswordVisible by remember { mutableStateOf(false) }

    var (forgotPasswordUid, forgotPasswordSetUid) = remember { mutableStateOf("") }
    val sendingForgotPasswordRequest by viewModel.sendingPasswordResetRequest

    MdcTheme {
        if (isInForgotPasswordFlow) {
            ForgotPasswordDialog(
                onDismiss = { viewModel.dismissForgotPasswordDialog() },
                isLoading = sendingForgotPasswordRequest,
                forgotPasswordUid = forgotPasswordUid,
                setForgotPasswordUid = forgotPasswordSetUid,
                onRequestLinkClick = {
                    viewModel.sendPasswordResetRequest(forgotPasswordUid)
                }
            )
        }
        LoginScreenView(
            isGoogleSigningIn = isGoogleSigningIn,
            isCredentialsSigningIn = isCredentialsSigningIn,
            signInFieldsEnabled = !(isGoogleSigningIn or isCredentialsSigningIn),
            onGoogleSignIn = onGoogleSignIn,
            uid = uid,
            setUid = setUid,
            password = password,
            setPassword = setPassword,
            isPasswordVisible = isPasswordVisible,
            onPasswordVisibilityToggled = {
                isPasswordVisible = !isPasswordVisible
            },
            onCredentialSignIn = {
                viewModel.signInWithCredentials(uid, password)
            },
            onForgotPassword = {
                forgotPasswordUid = ""
                viewModel.showForgotPasswordDialog()
            },
            onContactUsClick = {
                context.startActivity(
                    Intent.createChooser(
                        intent,
                        context.getString(R.string.send_email)
                    )
                )
            }
        )
    }
}


@Composable
fun LoginScreenView(
    isGoogleSigningIn: Boolean = false,
    isCredentialsSigningIn: Boolean = false,
    signInFieldsEnabled: Boolean = true,
    uid: String,
    setUid: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityToggled: () -> Unit,
    onGoogleSignIn: () -> Unit,
    onCredentialSignIn: () -> Unit,
    onForgotPassword: () -> Unit,
    onContactUsClick: () -> Unit
) {
    Surface(
        color = colorResource(id = R.color.colorBackground),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                SwdHeader()
                Spacer(modifier = Modifier.size(16.dp))
                PasswordLoginFields(
                    enabled = signInFieldsEnabled,
                    uid = uid,
                    setUid = setUid,
                    password = password,
                    setPassword = setPassword,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordVisibilityToggled = onPasswordVisibilityToggled,
                    onCredentialSignIn = onCredentialSignIn,
                    isSigningIn = isCredentialsSigningIn,
                    onForgotPassword = onForgotPassword
                )
                Spacer(modifier = Modifier.size(16.dp))
                OrDivider()
                GoogleSignInButton(
                    enabled = signInFieldsEnabled,
                    isSigningIn = isGoogleSigningIn,
                    onClick = onGoogleSignIn
                )
            }
            ContactUsPrompt(onClick = onContactUsClick)
        }
    }
}

@Composable
fun SwdHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_bits),
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier.padding(horizontal = 128.dp)
        )
        Text(
            text = stringResource(id = R.string.title_swd),
            style = MaterialTheme.typography.h5,
            color = colorResource(id = R.color.colorTextPrimary),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = stringResource(id = R.string.title_bphc),
            style = MaterialTheme.typography.subtitle1,
            color = colorResource(id = R.color.colorTextPrimary),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PasswordLoginFields(
    uid: String,
    setUid: (String) -> Unit,
    password: String,
    setPassword: (String) -> Unit,
    isPasswordVisible: Boolean = false,
    onPasswordVisibilityToggled: () -> Unit,
    enabled: Boolean = true,
    isSigningIn: Boolean = false,
    onCredentialSignIn: () -> Unit,
    onForgotPassword: () -> Unit
) {
    val textFieldModifier = Modifier
        .padding(horizontal = 24.dp, vertical = 4.dp)
        .fillMaxWidth()

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = uid,
            onValueChange = setUid,
            modifier = textFieldModifier,
            enabled = enabled,
            label = {
                Text(text = "User ID")
            },
            singleLine = true,
            placeholder = {
                Text(text = "f201xxxxx")
            }
        )
        OutlinedTextField(
            value = password,
            onValueChange = setPassword,
            modifier = textFieldModifier,
            enabled = enabled,
            singleLine = true,
            label = {
                Text(text = "Password")
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggled) {
                    Icon(
                        painter = painterResource(
                            id = if (isPasswordVisible)
                                R.drawable.outline_visibility_24
                            else R.drawable.outline_visibility_off_24
                        ),
                        contentDescription = null,
                    )
                }
            }
        )
        Spacer(modifier = Modifier.size(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            ForgotPasswordPrompt(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 24.dp)
                    .wrapContentWidth(Alignment.End),
                onClick = onForgotPassword
            )
        }
        Spacer(modifier = Modifier.size(24.dp))
        Button(
            onClick = onCredentialSignIn,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            if (isSigningIn) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(16.dp)
                )
            } else {
                Text(text = "Sign in")
            }
        }
    }
}

@Composable
fun ForgotPasswordPrompt(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Text(
        text = "Forgot password?",
        color = colorResource(id = R.color.colorAccent),
        style = MaterialTheme.typography.button,
        textAlign = TextAlign.End,
        modifier = modifier
            .clickable { onClick() }
    )
}

@Composable
fun ForgotPasswordDialog(
    forgotPasswordUid: String,
    setForgotPasswordUid: (String) -> Unit,
    onDismiss: () -> Unit,
    isLoading: Boolean = false,
    onRequestLinkClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Forgot password")
        },
        text = {
            Column {
                Text(
                    text = "Enter your User ID and we'll email a password reset link to you.",
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = forgotPasswordUid,
                    onValueChange = setForgotPasswordUid,
                    label = {
                        Text(text = "User ID")
                    },
                    singleLine = true,
                    placeholder = {
                        Text(text = "f201xxxxx")
                    }
                )
            }
        },
        confirmButton = {
            TextButton(
                modifier = Modifier.animateContentSize(),
                onClick = onRequestLinkClick
            )
            {
                if (isLoading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier
                            .size(16.dp)
                    )
                } else {
                    Text(text = "Confirm")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

@Composable
fun OrDivider() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier
                .padding(start = 24.dp, end = 8.dp)
                .weight(1f)
                .fillMaxWidth()
        )
        Text(
            text = "or",
            style = MaterialTheme.typography.caption,
            fontSize = 11.sp,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Divider(
            modifier = Modifier
                .padding(start = 8.dp, end = 24.dp)
                .weight(1f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun GoogleSignInButton(
    enabled: Boolean = true,
    isSigningIn: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .animateContentSize()
            .padding(top = 16.dp)
    ) {
        if (isSigningIn) {
            CircularProgressIndicator(
                strokeWidth = 2.dp,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Image(
                painter = painterResource(id = R.drawable.googleg_standard_color_18),
                contentDescription = "Google",
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("Sign in with your BITS email")
        }
    }
}

@Composable
fun ContactUsPrompt(onClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable { onClick() }
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.outline_email_24),
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "Facing problems? Email us",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.caption
        )
    }
}

@Preview(uiMode = UI_MODE_NIGHT_YES)
@Composable
fun PreviewLoginScreen() {
    MdcTheme {
        LoginScreenView(
            onGoogleSignIn = { },
            onCredentialSignIn = { },
            onForgotPassword = { },
            onContactUsClick = { },
            uid = "f20191204",
            setUid = { },
            password = "dasdas",
            setPassword = { },
            isPasswordVisible = false,
            onPasswordVisibilityToggled = { }
        )
    }
}
