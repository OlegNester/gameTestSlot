package com.rikoriko.rctst.common

import com.rikoriko.rctst.Interface.GitServices
import com.rikoriko.rctst.retrofit.GitClient

object GitUrl {
    private val GIT_URL = GlobalVariable.GIT_URL
    val gitService: GitServices
        get() = GitClient.getGitClient(GIT_URL).create(GitServices::class.java)
}