cmake_minimum_required(VERSION 3.22.1)
project("anr_sigquit")

add_library(anr_sigquit SHARED anr_sigquit.c)
target_link_libraries(anr_sigquit log)
