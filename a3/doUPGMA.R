library(cluster)
# set up graphics device
pdf("UPGMAtrees.pdf")
for (f in c("distL20.csv","distL50.csv","distL200.csv")){
	# read in matrix
	x = as.matrix(read.csv(f,row.names = 1,header = FALSE))
	colnames(x) <- rownames(x)
	# estimate tree
	estimatedTree = agnes(x, diss = TRUE)
	# plot tree
	plot(as.dendrogram(estimatedTree),main = paste(f,"reconstructed tree"))
}
dev.off()

