package org.wysko.autogeokt.gui.form.components

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun BulletSpacer() {
    Spacer(Modifier.width(8.dp))
    Text("•")
    Spacer(Modifier.width(8.dp))
}
