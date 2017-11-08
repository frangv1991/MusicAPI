package api.resources;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.NotFoundException;

import model.Playlist;
import model.Song;
import model.repository.MapPlaylistRepository;
import model.repository.PlaylistRepository;

import java.net.URI;
import java.util.Collection;
import java.util.List;

@Path("/songs")
public class SongResource {

    public static SongResource _instance = null;
    PlaylistRepository repository;

    private SongResource() {
        repository = MapPlaylistRepository.getInstance();
    }

    public static SongResource getInstance() {
        if (_instance == null) {
            _instance = new SongResource();
        }
        return _instance;
    }

    @GET
    @Produces("application/json")
    public Collection<Song> getAll() {
        return repository.getAllSongs();
    }

    @GET
    @Path("/{id}")
    @Produces("application/json")
    public Song get(@PathParam("id") String songId) {
        Song song = repository.getSong(songId);

        if (song == null) {
            throw new NotFoundException("The song with id=" + songId + " was not found");
        }

        return song;
    }

    @POST
    @Consumes("application/json")
    @Produces("application/json")
    public Response addSong(@Context UriInfo uriInfo, Song song) {
        if (song.getTitle() == null || "".equals(song.getTitle())) {
            throw new BadRequestException("The name of the song must not be null");
        }
        
        if (song.getArtist()== null || "".equals(song.getArtist())) {
            throw new BadRequestException("The artist of the song must not be null");
        }
        
        if (song.getAlbum()== null || "".equals(song.getAlbum())) {
            throw new BadRequestException("The album of the song must not be null");
        }
        
        if (song.getYear()== null || "".equals(song.getYear())) {
            throw new BadRequestException("The year of the song must not be null");
        }

        repository.addSong(song);

        // Builds the response. Returns the song the has just been added.
        UriBuilder ub = uriInfo.getAbsolutePathBuilder().path(this.getClass(), "get");
        URI uri = ub.build(song.getId());
        ResponseBuilder resp = Response.created(uri);
        resp.entity(song);
        
        return resp.build();
    }

    @PUT
    @Consumes("application/json")
    public Response updateSong(Song song) {
        Song oldSong = repository.getSong(song.getId());
        
        if (oldSong == null) {
            throw new NotFoundException("The song with id=" + song.getId() + " was not found");
        }

        // Update title
        if (song.getTitle() != null) {
            oldSong.setTitle(song.getTitle());
        }

        // Update artist
        if (song.getArtist()!= null) {
            oldSong.setArtist(song.getArtist());
        }
        
        // Update album
        if (song.getAlbum()!= null) {
            oldSong.setAlbum(song.getAlbum());
        }
        
        // Update year
        if (song.getYear()!= null) {
            oldSong.setYear(song.getYear());
        }

        return Response.noContent().build();
    }

    @DELETE
    @Path("/{id}")
    public Response removeSong(@PathParam("id") String songId) {
        Song toberemoved = repository.getSong(songId);
        
        if (toberemoved == null) {
            throw new NotFoundException("The song with id=" + songId + " was not found");
        } else {
            repository.deleteSong(songId);
        }

        return Response.noContent().build();
    }

}
