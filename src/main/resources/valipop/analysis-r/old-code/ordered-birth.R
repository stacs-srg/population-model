calcP <- function(x) {
  # taken from MASS library - as used to calculate P values in summary(loglm)
  if(x$df > 0L) {
    return(1 - pchisq(x$lrt, x$df) )
  } else { 
    return(1)
  }
}

# Read in the data
file <- commandArgs(TRUE)[1]
file <- "/Users/tsd4/OneDrive/cs/PhD/code/population-model/validated/src/main/resources/results/scoty/20170805-181149:368/tables/ob-CT.csv"

data = read.csv(file, sep = ',', header = T)

# Standardise the data
data$freq <- round(data$freq)
data <- data[which(data$freq != 0), ]
data <- data[which(data$Date >= 1855) , ]
data <- data[which(data$Date <= 2015) , ]
data <- data[which(data$Age != "0to14"), ]
data <- data[which(data$Age != "50+"), ]

# Analysis
library("MASS")

model = loglm(freq ~ Age * NPCIAP * CIY * Date, data = data)

p2 <- calcP(model)

model

if(p2 > 0.75) {
  return(p2)
}

model = step(model, direction = "both")

p3 <- calcP(model)

if(p3 > 0.75) {
  return(p3)
}

model = loglm(freq ~ Source * Date * Sex * Age * Died, data = data)

p4 <- calcP(model)

if(p4 > 0.75) {
  return(max(p2, p3))
}

model = step(model, direction = "both")

p5 <- calcP(model)

if(p5 > 0.75) {
  return(max(p2, p3))
} else {
  return(-1)
}