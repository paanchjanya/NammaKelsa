package com.example.nammakelsa.ui.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.nammakelsa.model.Worker
import com.example.nammakelsa.ui.components.*
import com.example.nammakelsa.ui.theme.NammaKelsaTheme
import com.example.nammakelsa.ui.theme.VibrantOrange

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable

@Composable
fun SearchFeedScreen(
    workers: List<Worker>,
    skills: List<String>,
    selectedSkill: String?,
    onSkillSelected: (String?) -> Unit,
    onWorkerClick: (Worker) -> Unit
) {
    NammaKelsaTheme {
        Scaffold(
            topBar = {
                KelsaGlassyHeader(title = "Namma Kelsa")
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // Skill Selection Row
                item {
                    Text(
                        text = "Browse by Skill",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(bottom = 24.dp)
                    ) {
                        item {
                            KelsaFilterChip(
                                text = "All",
                                selected = selectedSkill == null,
                                onClick = { onSkillSelected(null) }
                            )
                        }
                        items(skills) { skill ->
                            KelsaFilterChip(
                                text = skill,
                                selected = selectedSkill == skill,
                                onClick = { onSkillSelected(skill) }
                            )
                        }
                    }
                }

                // Workers Section
                item {
                    Text(
                        text = if (selectedSkill == null) "Top Workers" else "Top in $selectedSkill",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                }

                if (workers.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No workers found nearby",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }
                } else {
                    items(workers) { worker ->
                        WorkerCard(
                            worker = worker,
                            onClick = { onWorkerClick(worker) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WorkerCard(
    worker: Worker,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    KelsaCard(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            // Worker Info
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(20.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = worker.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = worker.skillType,
                        style = MaterialTheme.typography.bodyLarge,
                        color = VibrantOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "₹${worker.dailyRate}/day",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = if (worker.isAvailable) Color(0xFF34C759) else Color.Gray,
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.size(8.dp)
                    ) {}
                }
            }

            // Placeholder for Profile Photo
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(120.dp)
                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = worker.name.take(1),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SearchFeedPreview() {
    SearchFeedScreen(
        workers = listOf(
            Worker("1", "Ravi Kumar", "9876543210", "Plumber", 500, "Jayanagar", 12.92, 77.58, "", emptyList(), true),
            Worker("2", "Lakshmi S", "9876543211", "Cleaning", 400, "JP Nagar", 12.91, 77.59, "", emptyList(), false)
        ),
        skills = listOf("Plumber", "Electrician", "Cleaning", "Carpenter"),
        selectedSkill = null,
        onSkillSelected = {},
        onWorkerClick = {}
    )
}
